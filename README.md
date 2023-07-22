# RSSExpress

RSSExpress is a light-weight tool to fetch RSS feeds, you can get RSS feeds through REST API or have RSSExpress deliver it to your Telegram.

---

## Setup
### Mongodb
- RSSExpress uses MongoDB to store data.
The mongodb.properties file looks like:<br />
connection="your mongodb connection"

### RSSHub
- RSSExpress uses [RSSHub](https://github.com/DIYgod/RSSHub) to fetch RSS feeds from sources that don't provide RSS.
- The key "RSSHub" in application.properties designates the domain name of the RSSHub instance.
### Telegram
- Build your Telegram bot with the [official guide](https://core.telegram.org/bots).
- Build telegram.properties file with following content:<br />
  creater_id="your Telegram ID"<br />
  robot_name="your Telegram robot name"<br />
  token="your token for this robot"
- Basic Telegram commands:
    - /create (Create RSS list)
    - /help (Show commands)
    - /sub link (Subscribe RSS with the given link. Replace the domain name of RSSHub with "/rsshub" or "rsshub" )
    - /unsub link (Unsubscribe RSS with the given link)
    - /list (Show subscription list)
