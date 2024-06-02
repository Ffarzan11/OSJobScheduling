import java.util.Comparator;

class SJFComparator implements Comparator<JobObject> {

    public int compare(JobObject j1, JobObject j2) {
        return j1.cpuBurst > j2.cpuBurst ? 1:-1;
    }
}
