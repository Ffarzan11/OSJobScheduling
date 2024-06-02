import java.util.ArrayList;

public class JobObject {
    protected int arrTime = 0;
    protected int cpuBurst = 0;
    protected int priority = 0;
    protected int exitTime = 0;
    protected int turnAroundTime = 0;
    protected int remainingTime = 0;
    protected ArrayList<Integer> remainingTimes = new ArrayList<>();

    public JobObject() {

    }

    public JobObject(int arrTime, int cpuBurst, int priority, int remainingTime) {
        this.arrTime = arrTime;
        this.cpuBurst = cpuBurst;
        this.priority = priority;
        this.remainingTime = remainingTime;
    }


}

