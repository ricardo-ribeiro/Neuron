package uk.ricardo.ribeiro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ricardoribeiro on 23/02/2018.
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private List<WebSocketSession> sessions;
    private String parserUrl = "http://localhost:5000/v1/viz";
    public WebSocketHandler(List<WebSocketSession> sessions) {
        this.sessions = sessions;
        //this.parserUrl = parserUrl;
    }

    private RestTemplate rest = new RestTemplate();



    @Scheduled(fixedRate = 60000)
    public void reportCurrentTime() {
        //log.info("The time is now {}", dateFormat.format(new Date()));
        System.out.println("sending more data");
        ArrayList<WebSocketSession> sessionsToRemove = new ArrayList<WebSocketSession>();
        sessions.forEach(se ->{
            try {
                if(se.isOpen()){
                    se.sendMessage(new TextMessage(rest.getForObject(parserUrl,String.class)));
                }
                else{
                    sessionsToRemove.add(se);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        sessionsToRemove.forEach(webSocketSession -> {
            sessions.remove(webSocketSession);
        });
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        System.out.println(message);
        sessions.forEach(se ->{
            try {
                se.sendMessage(new TextMessage(rest.getForObject(parserUrl,String.class)));


            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //the messages will be broadcasted to all users.
        sessions.add(session);
        try {
            //rest.getForObject("localhost:5000/v1/viz",String.class);
            String response = rest.getForObject(parserUrl,String.class);
            session.sendMessage(new TextMessage(response));
            session.sendMessage(new TextMessage(response));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("hello");
        }
    }
}
