package robocode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithStatus;

import javax.xml.ws.Response;
import java.util.Map;

public abstract class GymRobotObservation {
    public String observation;
    public double reward;
    public boolean done;
    public Map<String,String> info;

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
