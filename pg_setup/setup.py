#!/usr/bin/env python3

import shutil
import os
import time

# Function to check if a directory is empty
def is_directory_empty(directory):
    return not os.listdir(directory)

# Function to copy directory contents recursively
def copy_contents(source, destination):
    print(f"Copying from {source} to {destination}...", flush=True)
    try:
        if is_directory_empty(destination):
            shutil.copytree(source, destination, dirs_exist_ok=True)
            print(f"Copy to {destination} successful", flush=True)
        else:
            print(f"Skipping copy to {destination}: Directory is not empty", flush=True)
    except Exception as e:
        print(f"Error copying to {destination}: {str(e)}", flush=True)

# Function to append or update a parameter in a config file
def update_config_file(filepath, parameter, value):
    try:
        updated = False
        with open(filepath, 'r') as file:
            lines = file.readlines()
            for i, line in enumerate(lines):
                if line.strip().startswith(parameter):
                    if '#' in line:
                        # Parameter is commented out, uncomment it
                        lines[i] = f"{parameter} = {value}\n"
                        updated = True
                    else:
                        # Parameter exists and is set, update it
                        lines[i] = f"{parameter} = {value}\n"
                        updated = True
                    break
            else:
                # Parameter not found, append it at the end of the file
                lines.append(f"{parameter} = {value}\n")
                updated = True
        
        if updated:
            with open(filepath, 'w') as file:
                file.writelines(lines)
                print(f"Updated {parameter} in {filepath}", flush=True)
        else:
            print(f"No changes needed for {parameter} in {filepath}", flush=True)
    except Exception as e:
        print(f"Error updating {parameter} in {filepath}: {str(e)}", flush=True)

# Paths to directories
pg_backup = '/pg_backup'
pg_master = '/pg_master'
pg_slave = '/pg_slave'
pg_asyncslave = '/pg_asyncslave'

# Path to flag file
flag_file = '/setup_done.flag'

# Check if setup is already done
if os.path.exists(flag_file):
    print("Setup already done. Sleeping indefinitely...", flush=True)
    while True:
        time.sleep(3600)  # Sleep for an hour and then repeat indefinitely

# Copy contents of /pg_backup to /pg_master, /pg_slave, /pg_asyncslave
copy_contents(pg_backup, pg_master)
copy_contents(pg_backup, pg_slave)
copy_contents(pg_backup, pg_asyncslave)

# Configure PostgreSQL settings on /pg_master
configurations = {
    "ssl": "off",
    "wal_level": "replica",
    "max_wal_senders": "4"
}

for param, value in configurations.items():
    update_config_file(os.path.join(pg_master, 'postgresql.conf'), param, value)

# Update pg_hba.conf on /pg_master
# update_config_file(os.path.join(pg_master, 'pg_hba.conf'), f"host replication replicator {PG_NET} md5", "")

# Create standby.signal file and configure settings on /pg_slave
open(os.path.join(pg_slave, 'standby.signal'), 'a').close()
update_config_file(os.path.join(pg_slave, 'postgresql.conf'), "primary_conninfo", "'host=pg_master port=5432 user=replicator password=pass application_name=pg_slave'")
# update_config_file(os.path.join(pg_slave, 'pg_hba.conf'), f"host replication replicator {PG_NET} md5", "")

# Create standby.signal file and configure settings on /pg_asyncslave
open(os.path.join(pg_asyncslave, 'standby.signal'), 'a').close()
update_config_file(os.path.join(pg_asyncslave, 'postgresql.conf'), "primary_conninfo", "'host=pg_master port=5432 user=replicator password=pass application_name=pg_asyncslave'")
# update_config_file(os.path.join(pg_asyncslave, 'pg_hba.conf'), f"host replication replicator {PG_NET} md5", "")

# Create flag file to indicate setup is done
with open(flag_file, 'w') as file:
    file.write('setup_done')

print("Setup completed successfully. Sleeping indefinitely...", flush=True)
while True:
    time.sleep(3600)  # Sleep for an hour and then repeat indefinitely
