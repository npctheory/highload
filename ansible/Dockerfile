# Use the willhallonline/ansible:latest Docker image as base, which is based on Alpine
FROM willhallonline/ansible:latest

# Install bash and docker
RUN apk add --no-cache bash docker iputils

# Install community.postgresql collection
RUN ansible-galaxy collection install community.postgresql

# Copy Ansible configuration files and playbooks
COPY ansible.cfg /ansible/ansible.cfg
COPY inventory /ansible/inventory
COPY playbooks /ansible/playbooks

# Command to keep the container running
CMD ["sleep", "infinity"]
