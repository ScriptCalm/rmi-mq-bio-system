package shared;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BioDetails implements Serializable {
    private String name;
    private String content;
    private LocalDateTime timestamp;

    // No-argument constructor (for serialization / frameworks)
    public BioDetails() {
    }

    // Full constructor
    public BioDetails(String name, String content) {
        this.name = name;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] Updated bio entry for " + name + ": \"" + content + "\"";
    }
}
