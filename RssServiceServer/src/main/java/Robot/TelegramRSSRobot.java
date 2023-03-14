package Robot;

import DBManager.DBManager;
import Models.RSSContent;
import Models.RSSItem;
import Models.RSSList;
import RSSAdapter.RSSAdapter;
import Tools.RSSExpressProperties;
import kotlin.Pair;
import org.mapdb.DB;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramRSSRobot extends TelegramLongPollingBot implements RSSRobot {

    private RSSAdapter adapter;

    private long creater_id;

    private String token;

    private String robot_name;

    private String robot_type = "";
    private int update_interval_sec = 3600;

    private ScheduledExecutorService service = null;


    public TelegramRSSRobot(int interval, RSSAdapter adapter) {
        this.adapter = adapter;
        this.robot_name = RSSExpressProperties.getRobotName();
        this.creater_id = RSSExpressProperties.getCreaterId();
        this.token = RSSExpressProperties.getTOKEN();
        this.setRobot_type("TELEGRAM");
        this.setUpdate_interval_sec(interval);
    }

    public String getRobot_type() {
        return robot_type;
    }

    public void setRobot_type(String robot_type) {
        this.robot_type = robot_type;
    }

    public int getUpdate_interval_sec() {
        return update_interval_sec;
    }

    public void setUpdate_interval_sec(int update_interval_sec) {
        this.update_interval_sec = update_interval_sec;
    }

    @Override
    public void startRobot() {
        Runnable runnable = () -> {
            // check out if we have new RSS contents
            this.checkRSSUpdate();
        };

        if (service == null)
            service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, this.update_interval_sec, TimeUnit.SECONDS);

    }

    @Override
    public void shutDownRobot() {
        service.shutdown();
    }

    private void checkRSSUpdate() {
        for (Pair<String, RSSList> pair : adapter.getRSSUpdate("telegram_id")) {

            for (RSSContent content : pair.getSecond().getRSSList()) {
                try {
                    sendLatest(Long.parseLong(pair.getFirst()), content);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            if (message.isCommand()) {
                String command = message.getText().split(" ")[0];
                switch (command) {
                    case "/create":
                        createList(update);
                        break;
                    case "/sub":
                        subscribe(update);
                        break;
                    case "/unsub":
                        unsubscribe(update);
                        break;
                    case "/list":
                        rssList(update);
                        break;
                    case "/help":
                        help(update);
                        break;
                    case "/start":
                        start(update);
                        break;
                    case "/stop":
                        stop(update);
                        break;
                    default:
                        sendError(update, "Command " + command + " unknown.");
                        break;
                }
            } else {
                sendError(update, "Unknown");
            }

        }
    }

    private void createList(Update update) {
        SendMessage message = new SendMessage();
        long user_id = update.getMessage().getChatId();

        message.setChatId(user_id);

        int owner_id = DBManager.createAccountRelation("telegram_id", String.valueOf(user_id));

        if (owner_id == -1) {
            message.setText("You already have a list.");
        } else {
            message.setText("RSS list was successfully created.");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(Update update) {
        Message message = update.getMessage();
        long user_id = message.getChatId();
        String[] command_string = message.getText().split(" ");

        if (command_string.length <= 1) {
            try {
                SendMessage send_message = new SendMessage();
                send_message.setChatId(user_id);
                send_message.setText("Please indicate the link of RSS.");
                execute(send_message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        int owner_id = DBManager.getAccountRelation("telegram_id", String.valueOf(user_id));

        try {
            RSSContent RSS_content = adapter.subscribe(owner_id, command_string[1]);
            SendMessage send_message = new SendMessage();
            send_message.setChatId(user_id);

            if (RSS_content != null) {
                send_message.setText("Subscribe success.");
                sendLatest(user_id, RSS_content);
            } else {
                send_message.setText("Subscribe failed.");
            }
            execute(send_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void unsubscribe(Update update) {
        Message message = update.getMessage();
        long user_id = message.getChatId();
        String[] command_string = message.getText().split(" ");

        if (command_string.length <= 1) {
            try {
                SendMessage send_message = new SendMessage();
                send_message.setChatId(user_id);
                send_message.setText("Please indicate the link of RSS.");
                execute(send_message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        int owner_id = DBManager.getAccountRelation("telegram_id", String.valueOf(user_id));

        try {
            boolean result = adapter.unsubscribe(owner_id, command_string[1]);
            SendMessage send_message = new SendMessage();
            send_message.setChatId(user_id);

            if (result) {
                send_message.setText("Unsubscribe success.");
            } else {
                send_message.setText("Unsubscribe failed.");
            }
            execute(send_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void rssList(Update update) {
        Long user_id = update.getMessage().getChatId();
        int owner_id = DBManager.getAccountRelation("telegram_id", String.valueOf(user_id));

        RSSList RSS_list = DBManager.getRSSList(owner_id);

        SendMessage message = new SendMessage();
        message.setChatId(user_id);
        message.setText(RSS_list.toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void help(Update update) {
        Long user_id = update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        message.setChatId(user_id);
        message.setText("/create (create RSS list)\n/sub link (subscribe RSS with link)\n/unsub link (unsubscribe)\n/list (show RSS list)\n/help (show commands)");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendLatest(long chatId, RSSContent RSS_content) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("html");
        message.setText(RSS_content.getLatest_link());
        execute(message);
    }

    private void start(Update update) {
        SendMessage message = new SendMessage();
        long user_id = update.getMessage().getChatId();

        message.setChatId(user_id);

        if (user_id == creater_id) {
            startRobot();
            message.setText(this.robot_name + " started.");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    private void stop(Update update) {
        SendMessage message = new SendMessage();
        long user_id = update.getMessage().getChatId();

        message.setChatId(user_id);

        if (user_id == creater_id) {
            shutDownRobot();
            message.setText(this.robot_name + " shut down.");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendError(Update update, String error) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(error);
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return robot_name;
    }

    @Override
    public String getBotToken() {
        return token;

    }
}
