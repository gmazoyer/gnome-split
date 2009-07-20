# If you need to debug some classpath, includes, or command line arguments
# option, then comment out MAKEFLAGS line below, or set V=1 on the command
# line before make.
#
ifdef V
else
MAKEFLAGS=-s
endif

.PHONY: all dirs compile translation install clean distclean

-include .config

all: .config dirs compile translation gsplit

.config: src/org/gnome/split/config/Constants.java
	/bin/echo
	/bin/echo "You need to run ./configure to check prerequisites"
	/bin/echo "and setup preferences before you can build gSplit."
	( if [ ! -x configure ] ; then chmod +x configure ; /bin/echo "I just made it executable for you." ; fi )
	/bin/echo
	exit 1

CLASSPATH=$(JAVA_GNOME_JAR):$(DBUS_JAVA_JAR):$(DEBUG_DISABLE_JAR):$(DEBUG_ENABLE_JAR):$(HEXDUMP_JAR):$(UNIX_SOCKET)

SOURCES_DIST=$(shell find src/ -name '*.java')
TRANSLATIONS=$(shell find po/  -name '*.po' | sed -e 's/po\/\(.*\)\.po/share\/locale\/\1\/LC_MESSAGES\/gsplit\.mo/g')

dirs: tmp/classes tmp/stamp tmp/i18n

tmp/classes:
	@/bin/echo -e "MKDIR\t$@"
	mkdir $@

tmp/stamp:
	@/bin/echo -e "MKDIR\t$@"
	mkdir $@

tmp/i18n:
	@/bin/echo -e "MKDIR\t$@"
	mkdir $@

# --------------------------------------------------------------------
# Source compilation
# --------------------------------------------------------------------

# Build the sources (that are part of the distributed app)
compile: tmp/stamp/compile
tmp/stamp/compile: $(SOURCES_DIST)
	@/bin/echo -e "$(JAVAC_CMD)\ttmp/classes/*.class"
	$(JAVAC) -d tmp/classes -classpath tmp/classes:$(CLASSPATH) -sourcepath src/ $^
	touch $@

translation: tmp/i18n/gnome-split.pot $(TRANSLATIONS)

# Strictly speaking, not necessary to generate the .pot file, but this has to
# go somewhere and might as well get it done
tmp/i18n/gnome-split.pot: $(SOURCES_DIST)
	@/bin/echo -e "EXTRACT\t$@"
	xgettext -o $@ --omit-header --keyword=_ --keyword=N_ $^

share/locale/%/LC_MESSAGES/gnome-split.mo: po/%.po
	mkdir -p $(dir $@)
	@/bin/echo -e "MSGFMT\t$@"
	msgfmt -o $@ $<

gsplit: tmp/launcher/gnome-split-local
	@/bin/echo -e "CP\t$@"
	cp -f $< $@
	chmod +x $@

# --------------------------------------------------------------------
# Installation
# --------------------------------------------------------------------

install: all \
		$(DESTDIR)$(JARDIR)/gnome-split-$(VERSION).jar \
	 	tmp/stamp/install-pixmaps \
	 	tmp/stamp/install-translations \
		$(DESTDIR)$(PREFIX)/share/applications/gnome-split.desktop \
		$(DESTDIR)$(PREFIX)/bin/gnome-split

$(DESTDIR)$(PREFIX):
	@/bin/echo -e "MKDIR\t$(DESTDIR)$(PREFIX)/"
	-mkdir -p $(DESTDIR)$(PREFIX)

$(DESTDIR)$(PREFIX)/bin:
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir -p $@

$(DESTDIR)$(JARDIR):
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir -p $@

$(DESTDIR)$(PREFIX)/share/applications:
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir -p $@

$(DESTDIR)$(PREFIX)/bin/gsplit: \
		$(DESTDIR)$(PREFIX)/bin \
		tmp/launcher/gnome-split-install
	@/bin/echo -e "INSTALL\t$@"
	cp -f tmp/launcher/gnome-split-install $@
	chmod +x $@

$(DESTDIR)$(PREFIX)/share/applications/gsplit.desktop: \
		$(DESTDIR)$(PREFIX)/share/applications \
		tmp/launcher/gnome-split.desktop
	@/bin/echo -e "INSTALL\t$@"
	cp -f tmp/launcher/gsplit.desktop $@

tmp/gsplit.jar: tmp/stamp/compile
	@/bin/echo -e "$(JAR_CMD)\t$@"
	$(JAR) -cf tmp/gnome-split.jar -C tmp/classes .

$(DESTDIR)$(PREFIX)/share/pixmaps: 
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir $@

$(DESTDIR)$(PREFIX)/share/locale: 
	@/bin/echo -e "MKDIR\t$@/"
	-mkdir $@

tmp/stamp/install-pixmaps: \
		$(DESTDIR)$(PREFIX)/share/pixmaps \
		share/pixmaps/*.png
	@/bin/echo -e "INSTALL\t$(DESTDIR)$(PREFIX)/share/pixmaps/*.png"
	cp -f share/pixmaps/*.png $(DESTDIR)$(PREFIX)/share/pixmaps
	chmod 644 $(DESTDIR)$(PREFIX)/share/pixmaps/gnome-split.png
	touch $@

tmp/stamp/install-translations: \
		$(DESTDIR)$(PREFIX)/share/locale \
		share/locale/*/LC_MESSAGES/gsplit.mo
	@/bin/echo -e "INSTALL\t$(DESTDIR)$(PREFIX)/share/locale/*/LC_MESSAGES/gnome-split.mo"
	cp -af share/locale/* $(DESTDIR)$(PREFIX)/share/locale
	touch $@

$(DESTDIR)$(JARDIR)/gsplit-$(VERSION).jar: \
		$(DESTDIR)$(JARDIR) \
		tmp/gnome-split.jar
	@/bin/echo -e "INSTALL\t$@"
	cp -f tmp/gnome-split.jar $@
	@/bin/echo -e "SYMLINK\t$(@D)/gsplit.jar -> gnome-split-$(VERSION).jar"
	cd $(@D) && rm -f gnome-split.jar && ln -s gnome-split-$(VERSION).jar gnome-split.jar

# --------------------------------------------------------------------
# House keeping
# --------------------------------------------------------------------

# [note that we don't remove .config here, as a) darcs doesn't pick it up
# so if it's hanging around it won't cause problems, and b) if it is removed 
# here, then `make clean all` fails]
clean:
	@/bin/echo -e "RM\ttemporary build directories"
	-rm -rf tmp/classes
	-rm -rf tmp/stamp
	-rm -rf hs_err_*
	@/bin/echo -e "RM\texecutables and wrappers"
	-rm -f tmp/gnome-split.jar
	-rm -f gnome-split
	@/bin/echo -e "RM\tgenerated message files"
	-rm -rf share/locale

distclean: clean
	@/bin/echo -e "RM\tbuild configuration information"
	-rm -f .config .config.tmp
	-rm -rf tmp/

# --------------------------------------------------------------------
# Distribution target
# --------------------------------------------------------------------

#
# Remember that if you bump the version number you need to commit the change
# and re-./configure before being able to run this! On the other hand, we
# don't have to distclean before calling this.
#
dist: all
	@/bin/echo -e "CHECK\tfully committed state"
	bzr diff > /dev/null || ( echo -e "\nYou need to commit all changes before running make dist\n" ; exit 4 )
	@/bin/echo -e "EXPORT\ttmp/gnome-split-$(VERSION)"
	-rm -rf tmp/gnome-split-$(VERSION)
	bzr export --format=dir tmp/gnome-split-$(VERSION)
	@/bin/echo -e "TAR\gnome-split-$(VERSION).tar.gz"
	tar cvf gnome-split-$(VERSION).tar.gz -C tmp gnome-split-$(VERSION)
	rm -r tmp/gnome-split-$(VERSION)
