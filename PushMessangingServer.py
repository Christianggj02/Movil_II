import tkinter as tk
from google.auth.transport.requests import Request
from google.oauth2 import service_account
import requests
import json

# Token cel 1
# cdq7wFQWTlek9eB_3ihzYX:APA91bEUfoF1dA_nuk75Q1tAZ6gvKDyDJYP6BwRWEh-TL6-ePGd-LPPyC6hQg03i-HKE6nGD47-XbXIUfTUFa5_5RXBPcjUNXkaOLblJryq6hpztXCHoZ0sMOYZBtahswKFpP9l8YzLU
# Token cel 2
# fIl2aBGlTyWWblRlxOb8YB:APA91bHpQyNu35V-AJj3lh4W4vwCFcLktowJwQVg1usuWSHQlcrSXbwEHwdlf-8RzttfGXc753ZhiK6k1pZwkbj5C1O7yMiFTn3SjWeRdHIGNc2OiiAnOPI76tq6umOzZ8oXXvKSIyoJ

def enviar_mensaje():
    credentials = service_account.Credentials.from_service_account_file(
        'C:\\Users\\Chris\\Documents\\8vo Semestre\\Movil II\\PushMessanging\\mensajes-d8fb3-7db356395b03.json',
        scopes=['https://www.googleapis.com/auth/firebase.messaging']
    )
    credentials.refresh(Request())

    project_id = 'mensajes-d8fb3'
    base_url = f'https://fcm.googleapis.com/v1/projects/{project_id}/messages:send'

    message_payload = {
        'message': {
            'token': entry_id.get(),
            'notification': {
                'title': entry_titulo.get(),
                'body': entry_cuerpo.get()
            }
        }
    }

    response = requests.post(
        base_url,
        headers={
            'Authorization': f'Bearer {credentials.token}',
            'Content-Type': 'application/json; UTF-8',
        },
        data=json.dumps(message_payload)
    )

    print('Respuesta:', response.text)

root = tk.Tk()
root.title("Enviar Mensaje Push")

tk.Label(root, text="Token del Dispositivo:").pack()
entry_id = tk.Entry(root)
entry_id.pack()

tk.Label(root, text="Título del Mensaje:").pack()
entry_titulo = tk.Entry(root)
entry_titulo.pack()

tk.Label(root, text="Cuerpo del Mensaje:").pack()
entry_cuerpo = tk.Entry(root)
entry_cuerpo.pack()

button_enviar = tk.Button(root, text="Enviar Mensaje", command=enviar_mensaje)
button_enviar.pack()

root.mainloop()
