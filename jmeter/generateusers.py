import csv
import random
import string
from faker import Faker
import os

# Initialize Faker to generate random data
fake = Faker()

# Define the number of records
TARGET_SIZE_MB = 1
RECORD_SIZE_BYTES = 150  # Average size of a record in bytes (approximate)
NUM_RECORDS = (TARGET_SIZE_MB * 1024 * 1024) // RECORD_SIZE_BYTES  # Calculate number of records to make 1MB

# Define the output file
output_file = 'users.csv'

# Define the genders available
genders = ['male', 'female']

# Function to generate random user data
def generate_user_data():
    username = fake.user_name()
    password = ''.join(random.choices(string.ascii_letters + string.digits, k=12))
    name = fake.first_name()
    surname = fake.last_name()
    date_of_birth = fake.date_of_birth(minimum_age=18, maximum_age=90).isoformat()
    gender = random.choice(genders)
    interests = fake.word()  # Generate a single lowercase word
    city = fake.city()
    return [username, password, name, surname, date_of_birth, gender, interests, city]

# Write to CSV
with open(output_file, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['username', 'password', 'name', 'surname', 'dateOfBirth', 'gender', 'interests', 'city'])
    for _ in range(NUM_RECORDS):
        writer.writerow(generate_user_data())

# Check the size of the generated file
file_size = os.path.getsize(output_file) / (1024 * 1024)  # Convert bytes to megabytes
print(f'Generated file size: {file_size:.2f} MB')
