import java.util.Random;

public class Main {
    public static void main(String[] args) {

        Random rand = new Random();
        JobObject[] jobs = new JobObject[25];
        for(int i = 0; i < 25; i++) {
            int arrTime = rand.nextInt(250) + 1;
            int cpuBurst = rand.nextInt(14)+2;
            int priority = rand.nextInt(5) + 1;
            jobs[i] = new JobObject(arrTime,cpuBurst,priority, cpuBurst);
        }

        System.out.println("JOB INFORMATION");
        printArr(jobs);
        System.out.println();

        FIFO f1 = new FIFO(jobs);
        f1.setJobTimes();
        f1.print();

        SJF f2 = new SJF(jobs);
        f2.setJobTimes();
        f2.print();


        SRT f3 = new SRT(jobs);
        f3.setJobTimes();
        f3.print();

        RoundRobins f4 = new RoundRobins(jobs);
        f4.setJobTimes();
        f4.print();


        RRNoContextSwitch f5 = new RRNoContextSwitch(jobs);
        f5.setJobTimes();
        f5.print();


        HighestPriority f6 = new HighestPriority(jobs);
        f6.setJobTimes();
        f6.print();

    }
    public static void printArr(JobObject[] arr) {
        System.out.println("| Arrival time\t| CPU Burst\t| Exit\t| Turn Around Time\t| Remaining Time\t");
        for(JobObject job: arr) {
            System.out.print("|\t\t"+job.arrTime + "\t\t|\t");
            System.out.print(job.cpuBurst + "\t\t|\t");
            System.out.print(job.exitTime + "\t|\t");
            System.out.print("\t"+ job.turnAroundTime + "\t\t\t|\t");
            System.out.print("\t"+ job.remainingTime + " \t\t|");
            System.out.println("\t\t\t");
        }
    }
}
