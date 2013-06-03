#!/bin/bash

if [ "x$APPLICATION_DIR" = "x" ]; then
    echo "ERROR: APPLICATION_DIR has to be set"
    exit 1
fi

/usr/bin/id
/bin/hostname

# first some basic setup
mkdir -p $APPLICATION_DIR
mkdir -p $APPLICATION_DIR/logs

rc=0

for PACKAGE in `cat ./install-scripts/.applications`; do

    # ignore commented out lines
    if [[ $PACKAGE == \#* ]]; then
	continue;
    fi

    echo
    echo "Starting install of $PACKAGE"
    chmod 755 ./install-scripts/$PACKAGE
    if ./install-scripts/$PACKAGE >$APPLICATION_DIR/logs/$PACKAGE.build.log 2>&1; then
        echo "Success build of $PACKAGE"
    else
        echo "Build failed with exit code $?. 100 lines of output:"
        tail -n 100 $APPLICATION_DIR/logs/$PACKAGE.build.log
        rc=1
    fi

done

exit $rc

