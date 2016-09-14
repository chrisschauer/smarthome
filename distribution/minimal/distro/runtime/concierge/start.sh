#!/bin/sh

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`

RUNTIME_FOLDER=`cd "$DIRNAME/.."; pwd`
BASE_FOLDER=`cd "$RUNTIME_FOLDER/.."; pwd`

# JVM options
ARGS="-Dsmarthome.logdir=$BASE_FOLDER/userdata/logs -Dsmarthome.servicepid=org.eclipse -Djetty.etc.config.urls=etc/jetty.xml,etc/jetty-deployer.xml,etc/jetty-selector.xml"

# set ports for HTTP(S) server
# TODO: check if already set
HTTP_PORT=8080
HTTPS_PORT=8443

MAIN=$(find framework -name "org.eclipse.concierge-5.0.0*.jar" | sort | tail -1);

java $DEBUG_OPTS $ARGS \
	-Dosgi.clean=true \
	-Dorg.osgi.framework.storage=$BASE_FOLDER/userdata/storage \
	-Declipse.ignoreApp=true \
	-Dosgi.noShutdown=true \
	-Dsmarthome.userdata=$BASE_FOLDER/userdata \
	-Dlogback.configurationFile=$RUNTIME_FOLDER/etc/logback_debug.xml \
	-Dsmarthome.servicecfg=$RUNTIME_FOLDER/etc/services.cfg \
	-Dorg.quartz.properties=$RUNTIME_FOLDER/etc/quartz.properties \
	-DmdnsName=smarthome \
	-Djetty.home=$RUNTIME_FOLDER/ \
	-Dorg.osgi.service.http.port=$HTTP_PORT \
	-Dorg.osgi.service.http.port.secure=$HTTPS_PORT \
	-Dfelix.fileinstall.dir=$BASE_FOLDER/addons \
	-Djava.library.path=lib \
	-Dfelix.fileinstall.active.level=4 \
	-Djava.awt.headless=true \
	-Dfile.encoding="UTF-8" \
	-jar $MAIN $DIRNAME/smarthome.xargs


