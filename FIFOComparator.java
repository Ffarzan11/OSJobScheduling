import java.util.Comparator;

class FIFOComparator implements Comparator<JobObject> {

    public int compare(JobObject j1, JobObject j2) {
       return j1.arrTime > j2.arrTime ? 1:-1;
    }
}
