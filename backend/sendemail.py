import os
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

HOST = "smtp.gmail.com"
PORT = 587

FROM_EMAIL = "diwaskunwarmail@gmail.com"
TO_EMAIL = "travelsathiapp@gmail.com"
PASSWORD = "tpkv ykat fvpb fjdo "

message = MIMEMultipart("alternative")
message["From"] = FROM_EMAIL
message["To"] = TO_EMAIL
message["Subject"] = "Travel Sathi Password Reset Authentication"  

html = ""
with open("mail.html", "r") as file:
    
    html = file.read()

html_part = MIMEText(html, 'html')
message.attach(html_part)

try:
    smtp = smtplib.SMTP(HOST, PORT)
    smtp.starttls()
    smtp.login(FROM_EMAIL, PASSWORD)

    status_code, response = smtp.ehlo()
    print(f"[*] Echoing from Server: {status_code} {response}")

    smtp.sendmail(FROM_EMAIL, TO_EMAIL, message.as_string())
    print("Email sent successfully!")
    smtp.quit()

except Exception as e:
    print(f"Failed to send email. Error: {e}")
