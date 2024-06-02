import java.util.Comparator;

class SRTComparator implements Comparator<JobObject> {

    public int compare(JobObject j1, JobObject j2) {
        if(j1.remainingTime > j2.remainingTime) return  1;
        else if (j1.remainingTime < j2.remainingTime) {
            return -1;
        }
        else if( j1.arrTime < j2.arrTime) {
            return -1;
        }
        return  1;
//        return j1.remainingTime > j2.remainingTime ? 1:-1;
    }
}
