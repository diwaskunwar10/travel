from fastapi import FastAPI
from pydantic import BaseModel
import mysql.connector
import math
from typing import List
import pymysql
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from passlib.hash import bcrypt


app = FastAPI()

# Database connection configuration

db = pymysql.connect(host='localhost',
                     user='root',
                     password='',
                     database='travel',
                     cursorclass=pymysql.cursors.DictCursor)

# Define the Location model

class Location(BaseModel):
    latitude: float
    longitude: float

# Function to establish connection and fetch places data

def fetch_places_data():
    mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="travel"
    )
    
    cursor = mydb.cursor()
    cursor.execute("SELECT Name, Latitude, Longitude FROM places")
    return cursor.fetchall()

# Function to calculate distance using the provided 'distance' method

def distance(origin, destination):
    lat1, lon1 = origin
    lat2, lon2 = destination
    radius = 6371  # km
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat / 2) * math.sin(dlat / 2) + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2) * math.sin(dlon / 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    d = radius * c

    return d

# API route to get the top 5 nearest places

@app.post("/get_location")

async def get_location(location: Location):   
  
    latitude = location.latitude
    longitude = location.longitude
    # latitude = 27.6503928
    # longitude = 85.3207649
    
    print(f"User Location - Latitude: {latitude}, Longitude: {longitude}")  # Print user location
    
    
    places_data = fetch_places_data()  # Fetch places data from the database
    
    places = []
    for place in places_data:
        place_name, place_latitude, place_longitude = place
        place_distance = distance((latitude, longitude), (place_latitude, place_longitude))
        place_distance = round(place_distance, 3)
        places.append({"name": place_name, "distance": place_distance})
    
    # Sort places by distance
    places.sort(key=lambda x: x['distance'])
    
    if not places:
        return {"error": "No places found within the specified distance"}
    else:
        # closest_place = places[1]
        # print(f"Closest Place: {closest_place['name']} - Distance: {closest_place['distance']} km")
        
        # Get the top 5 closest places
        top_5_nearest = places[:5]
        print("Top 5 Nearest Places:", top_5_nearest)
        
        return { "top_5_nearest": top_5_nearest}
    
 #   
@app.get('/get_temple_data')
def get_temple_data() -> List[dict]:
    try:
        with db.cursor() as cursor:
            # SQL query to fetch data based on "Temple" category
            sql = "SELECT image, Name, description FROM places WHERE category LIKE '%Temple%'"
            cursor.execute(sql)
            temple_data = cursor.fetchall()
            print("temple Data",temple_data)
            return temple_data
           
    except Exception as e:
        return [{'error': str(e)}]
    
@app.get('/get_natural_data')
def get_natural_data() -> List[dict]:
    try:
        with db.cursor() as cursor:
            # SQL query to fetch data based on "Natural" category
            sql = "SELECT image, Name, description FROM places WHERE category LIKE '%Natural%'"
            cursor.execute(sql)
            natural_data = cursor.fetchall()
            print("Natural Places",natural_data)
            return natural_data
    except Exception as e:
        return [{'error': str(e)}]
    
        

@app.get('/get_lake_data')
def get_lake_data() -> List[dict]:
    try:
        with db.cursor() as cursor:
            # SQL query to fetch data based on "Lake" category  
            sql = "SELECT image, Name, description FROM places WHERE category LIKE '%Lake%'"
            cursor.execute(sql)
            lake_data = cursor.fetchall()
            return lake_data
    except Exception as e:
        return [{'error': str(e)}]
  
        




class UserEmail(BaseModel):
    email: str

def send_email(user_email):
    # Your email sending logic here
    print(f"Sending email to: {user_email}")
    
class UserEmailOTP(BaseModel):
    email: str
    otp: str
    
@app.post('/reset_password')
async def reset_password(user_data: UserEmailOTP):
    user_email = user_data.email
    user_otp = user_data.otp
    send_email(user_email,user_otp)
    print(f"message: Password reset initiated for email:   {user_email}+{user_otp}" )
    # Use user_email and user_otp for password reset logic or email sending logic
    
    return {"message": "Password reset initiated for email: " + user_email}


import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

def send_email(user_email,user_otp):
    HOST = "smtp.gmail.com"
    PORT = 587

    FROM_EMAIL = "diwaskunwarmail@gmail.com"
    PASSWORD = "tpkv ykat fvpb fjdo "
    TO_EMAIL = user_email

    message = MIMEMultipart("alternative")
    message["From"] = FROM_EMAIL
    message["To"] = TO_EMAIL
    message["Subject"] = "Travel Sathi Password Reset Authentication"

    # Constructing the email message with OTP
    html = f"""
    <html>
      <body>
        <p>Your OTP for password reset: {user_otp}</p>
      </body>
    </html>
    """

    html_part = MIMEText(html, 'html')
    message.attach(html_part)

    try:
        smtp = smtplib.SMTP(HOST, PORT)
        smtp.starttls()
        smtp.login(FROM_EMAIL, PASSWORD)

        status_code, response = smtp.ehlo()
        print(f"[*] Echoing from Server: {status_code} {response}")

        smtp.sendmail(FROM_EMAIL, user_email, message.as_string())
        print("Email sent successfully!")
        smtp.quit()

    except Exception as e:
        print(f"Failed to send email. Error: {e}")

# Replace 'user_email_here' and 'otp_here' with actual values
send_email('user_email_here', 'otp_here')


from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import JSONResponse
import mysql.connector
import base64


# MySQL database configuration
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '',
    'database': 'travel',
}

@app.post("/upload_data")
async def upload_data(title: str = Form(...), description: str = Form(...), category: str = Form(...), image: UploadFile = File(...)):
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()

        image_data = image.file.read()
        image_base64 = base64.b64encode(image_data).decode('utf-8')
        latitude, longitude = get_coordinates(title)
        # Insert data into the database
        sql = "INSERT INTO places (Name, description, category,latitude,longitude, image) VALUES (%s, %s, %s, %s,%s,%s)"
        val = (title, description, category, image_base64)
        cursor.execute(sql, val)
        conn.commit()

        # Close the database connection
        cursor.close()
        conn.close()

        return JSONResponse(content={"message": "Data inserted successfully"}, status_code=200)

    except Exception as e:
        return JSONResponse(content={"message": f"Error: {e}"}, status_code=500)
    
    
    
def get_coordinates(address):
    base_url = "https://nominatim.openstreetmap.org/search"
    params = {
        "format": "json",
        "q": address
    }

    response = requests.get(base_url, params=params)
    if response.status_code == 200:
        data = response.json()
        if data:
            # Extract latitude and longitude
            latitude = data[0]["lat"]
            longitude = data[0]["lon"]
            return float(latitude), float(longitude)
        else:
            return None, None
    else:
        return None, None


@app.get("/get_place_info/")
def get_place_info(name: str):
    try:
        with db.cursor() as cursor:
            # Execute SQL query
            sql = f"SELECT image, description, Name FROM places WHERE Name = '{name}'"
            cursor.execute(sql)

            # Fetch results
            result = cursor.fetchall()

            if result:
                return result
            else:
                return {"error": f"No matching data found for name: {name}"}

    except pymysql.Error as e:
        return {"error": f"Query failed: {str(e)}"}

@app.on_event("shutdown")
def shutdown_event():
    db.close()
    
    


@app.post("/adminsignup")
async def signup(name: str, email: str, password: str):
    cursor = db.cursor()

    try:
        # Check if email already exists
        cursor.execute("SELECT * FROM admin_reg WHERE email = %s", (email,))
        user = cursor.fetchone()

        if user:
            raise HTTPException(status_code=400, detail="Email already registered")

        hashed_password = bcrypt.hash(password)  # Hash the password
        
        insert_query = "INSERT INTO admin_reg (name, email, password) VALUES (%s, %s, %s)"
        insert_data = (name, email, hashed_password)
        
        cursor.execute(insert_query, insert_data)
        db.commit()

        cursor.close()  # Close the cursor after successful execution

        return {"message": "Signup successful"}
    except Exception as e:
        cursor.close()  # Close the cursor in case of an exception
        raise HTTPException(status_code=500, detail="Signup failed")