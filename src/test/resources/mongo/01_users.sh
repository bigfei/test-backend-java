#!/bin/bash

mongoimport --db appdb --collection users --drop \
            --file /docker-entrypoint-initdb.d/01_users.json \
            --jsonArray