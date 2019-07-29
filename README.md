# LiveNotificationBot
支持BiliBili、Youtube、Twitch直播提醒的DiscordBot

## Usage
![Loading Example](https://i.bmp.ovh/imgs/2019/05/a2af4f6d920f7ff7.gif)

## Setup

1. Install Java JDK 1.8
2. Download [live-notification.jar](https://github.com/sola97/LiveNotificationBot/blob/master/target/live-notification-1.0-SNAPSHOT-jar-with-dependencies.jar)
3. Edit the config file `config.txt`
```
token = BOT_TOKEN_HERE
owner = Your User ID
prefix = "!"
check-interval = 10
game = "BiliBili Youtube Twitch"
proxy = ""
//如果需要代理
//proxy = "http://127.0.0.1:1080" 
```

4.Run Bot  
 `java -jar live-notification-1.0-SNAPSHOT-jar-with-dependencies.jar`
