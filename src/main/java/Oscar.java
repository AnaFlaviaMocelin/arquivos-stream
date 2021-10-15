import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;

@Setter
@Getter
@AllArgsConstructor
@ToString

public class Oscar{
    private int id;
    private String name;
    private int age;
    private int year;
    private String movie;
}
