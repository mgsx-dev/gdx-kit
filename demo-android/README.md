
# Credentials

Google play service credentials are stored in **assets/openworld/client_secrets.json** file and is
ignored by GIT. This file is only included in desktop distribution.
In order to work, the local file should be created are respond to the official format :

```
{
  "installed": {
    "client_id": "xxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com",
    "client_secret": "yyyyyyyyyyyyyyyyyyyyyyyyyyyy"
  }
}
```