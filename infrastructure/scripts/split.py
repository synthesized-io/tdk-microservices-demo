import psycopg2

# Update these values with your PostgreSQL connection details
db_host = "localhost"
db_name = "postgres"
db_user = "postgres"
db_password = "123"

# Connect to the Pagila database
conn = psycopg2.connect(host=db_host, dbname=db_name, user=db_user, password=db_password)
conn.autocommit = True
cursor = conn.cursor()

# Create the new databases for films and payments
cursor.execute(f"CREATE DATABASE pagila_films WITH TEMPLATE {db_name}")
cursor.execute(f"CREATE DATABASE pagila_payments WITH TEMPLATE {db_name}")
conn.commit()

# Connect to the new databases
conn_films = psycopg2.connect(host=db_host, dbname="pagila_films", user=db_user, password=db_password)
cursor_films = conn_films.cursor()

conn_payments = psycopg2.connect(host=db_host, dbname="pagila_payments", user=db_user, password=db_password)
cursor_payments = conn_payments.cursor()

# Define tables related to films and payments
film_tables = ["film", "film_actor", "film_category", "actor", "category", "language"]
payment_tables = ["payment", "rental", "inventory", "store", "staff", "address", "city", "country", "customer"]

# Remove payment-related tables from the films database
for table in payment_tables:
    cursor_films.execute(f"DROP TABLE IF EXISTS {table} CASCADE")
conn_films.commit()

# Remove film-related tables from the payments database
for table in film_tables:
    cursor_payments.execute(f"DROP TABLE IF EXISTS {table} CASCADE")
conn_payments.commit()

# Close all connections
cursor.close()
cursor_films.close()
cursor_payments.close()
conn.close()
conn_films.close()
conn_payments.close()

print("Successfully split Pagila database into two databases: pagila_films and pagila_payments")
