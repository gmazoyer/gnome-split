package org.gnome.split.core;

import java.io.File;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import org.gnome.split.core.action.FileSplitter;
import org.gnome.split.core.algorithm.DotNumberAlgorithm;
import org.gnome.split.core.display.SplitterDisplay;
import org.gnome.split.core.event.ChunkCreatedEvent;
import org.gnome.split.core.event.ErrorSplittingEvent;
import org.gnome.split.core.event.ReadEvent;
import org.gnome.split.core.event.StartSplittingEvent;
import org.gnome.split.core.event.StopSplittingEvent;
import org.gnome.split.core.event.WriteEvent;
import org.gnome.split.core.util.ChunksList;
import org.gnome.split.core.util.SizeUnit;

import static org.freedesktop.bindings.Internationalization._;

public class Splitter implements Observer, Runnable
{
    private SplitterDisplay display;

    private String targetPath;

    private String filename;

    private long chunkSize;

    private long fileSize;

    private FileSplitter splitter;

    private ChunksList chunks;

    private Thread thread;

    public Splitter(SplitterDisplay display, String filename, String targetPath, long chunkSize) {
        this.display = display;
        this.targetPath = new String(targetPath);
        this.filename = new String(filename);
        this.chunkSize = chunkSize;
        this.splitter = null;
        this.chunks = null;
        this.thread = new Thread(this);
        this.thread.start();
    }

    private void removeFile(String name) {
        new File(name).delete();
    }

    private void finish(boolean error) {
        if (!error) {
            display.showMessage(_("Splitting done."));
        }

        display.setReadProgress(0);
        display.setWriteProgress(0);

        targetPath = null;
        filename = null;
        chunks = null;

        splitter.deleteObservers();

        splitter = null;
        thread = null;

        display.splittingFinished(error);
    }

    public void stopSplitting() {
        if (thread != null) {
            splitter.stopSplitting();
        }
    }

    public void removeChunks() {
        Enumeration<String> chunksEnum = chunks.getChunks();
        while (chunksEnum.hasMoreElements()) {
            this.removeFile((String) chunksEnum.nextElement());
        }
    }

    @Override
    public void update(Observable observable, Object object) {
        if (object instanceof ErrorSplittingEvent) {
            display.showError(_("Error splitting [{0}].", ((ErrorSplittingEvent) object).toString()));
            this.removeChunks();
            this.finish(true);
        } else if (object instanceof StartSplittingEvent) {
            StartSplittingEvent event = (StartSplittingEvent) object;
            fileSize = event.getFileSize();

            display.showMessage(_("Splitting {0} [{1}] on {2} chunks.", event.getFilename(),
                    SizeUnit.formatSize(fileSize), event.getNumberOfChunks()));
        } else if (object instanceof StopSplittingEvent) {
            this.finish(false);
        } else if (object instanceof ChunkCreatedEvent) {
            ChunkCreatedEvent event = (ChunkCreatedEvent) object;
            chunkSize = event.getFileSize();

            display.showMessage(_("Creating {0} [{1}].", event.getFileName(),
                    SizeUnit.formatSize(chunkSize)));
            display.chunkCreated(event.getFileName());
            display.setWriteProgress(0);
        } else if (object instanceof WriteEvent) {
            long written = ((WriteEvent) object).getBytesWritten();
            double progress = (double) written / (double) chunkSize;

            display.setWriteProgress(progress);
            display.setWriteInfos(written, chunkSize);
        } else if (object instanceof ReadEvent) {
            long read = ((ReadEvent) object).getBytesRead();
            double progress = (double) read / (double) fileSize;

            display.setReadProgress(progress);
            display.setReadInfos(read, fileSize);
        }
    }

    @Override
    public void run() {
        splitter = new FileSplitter();
        chunks = new ChunksList(splitter);

        splitter.addObserver(this);
        splitter.split(filename, chunkSize, new DotNumberAlgorithm(targetPath), true);
    }
}
