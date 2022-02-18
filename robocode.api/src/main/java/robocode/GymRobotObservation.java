package robocode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithStatus;

import javax.xml.ws.Response;
import java.util.Map;

public class GymRobotObservation {
    public GymRobotObservation(String observation, double reward, boolean done, Map<String, String> info) {
        this.observation = observation;
        this.reward = reward;
        this.done = done;
        this.info = info;
    }

    private String observation;
    private double reward;
    private boolean done;
    private Map<String,String> info;

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }


    public RsWithHeader toResponse() throws JsonProcessingException {
            return new RsWithHeader(
                    new RsWithBody(
                            new RsWithStatus(200),
                            new ObjectMapper().writeValueAsString(this)
                    ),
                    "Content-Type", "application/json"
            );
        }
}
