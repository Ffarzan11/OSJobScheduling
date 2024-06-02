import java.util.Comparator;

class HighestPriorityComparator implements Comparator<JobObject> {

    public int compare(JobObject j1, JobObject j2) {
        return j1.priority < j2.priority ? 1:-1;
    }
}
