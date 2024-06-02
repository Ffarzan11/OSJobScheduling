import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;

public class FIFO {

    protected PriorityQueue<JobObject> jobs = new PriorityQueue<>(new FIFOComparator());
    protected PriorityQueue<JobObject> ready = new PriorityQueue<>(new FIFOComparator());
    protected Deque<JobObject> running = new ArrayDeque<>();
    protected PriorityQueue<JobObject> complete = new PriorityQueue<>(new FIFOComparator());
    protected double totalTurnAround = 0;
    protected double count = 0;
    protected int timer = 0;

    //default constructor
    public FIFO() {

    }

    /**
     *
     * @param jobs the array of jobs which will be put in a priority queue
     */
    public FIFO(JobObject[] jobs) {
        JobObject[] jobsFifo = new JobObject[jobs.length];
        for(int i = 0; i < jobs.length; i++) {
            jobsFifo[i] = new JobObject(jobs[i].arrTime, jobs[i].cpuBurst, jobs[i].priority, jobs[i].remainingTime);
        }
        this.jobs.addAll(Arrays.asList(jobsFifo));
    }


    public void setJobTimes(){
        // run till there is no more job left in the job stack
        while(!jobs.isEmpty()) {
            JobObject current = null;
            JobObject next = null;

            if(timer == jobs.peek().arrTime) {
                current = jobs.poll();
                running.push(current); // add to running only if it's time arrives
            }

            //if there is more than one job, then next job is the job right after current in the stack
            if(!jobs.isEmpty()) {
                next = jobs.peek();
            }
            //while something is running continue the loop
            while (!running.isEmpty() || (!jobs.isEmpty()  && timer == jobs.peek().arrTime)) {

                if(ready.isEmpty() && running.isEmpty()) {
                    current = jobs.poll();
                    running.push(current);
                }

                while(next != null && timer == next.arrTime) {
                    ready.add(jobs.poll()); // add the ready job to readylist
                    if(!jobs.isEmpty()) {
                        next = jobs.peek(); // updating the next job

                    }
                    else{
                        next = null; //no more job left
                    }
                }
                //check if the current running job is completed or not
                if (current.remainingTime == 0) {
                    current.exitTime = timer;
                    current.turnAroundTime = current.exitTime - current.arrTime;
                    totalTurnAround += current.turnAroundTime;
                    running.pop();
                    complete.add(current); // add to completed stack

                    if(timer <= 100) {
                        count+=1;
                    }

                    //if job and ready queue is non empty and the next job in job stack has arrived
                    if(!ready.isEmpty()) {
                        current = ready.poll();
                        running.push(current);
                    }
                }

                timer += 1;
                current.remainingTime -= 1;
            }
            timer += 1;
        }
    }

    public void print() {
        System.out.println("FIFO Scheduling");
        System.out.println("| Arrival time\t| CPU Burst\t| Exit\t| Turn Around Time\t|");
        while (!complete.isEmpty()) {
            JobObject job = complete.poll();
            System.out.print("|\t\t"+job.arrTime + "\t\t|\t");
            System.out.print(job.cpuBurst + "\t\t|\t");
            System.out.print(job.exitTime + "\t|\t");
            System.out.println("\t"+ job.turnAroundTime + "\t\t\t|\t");
        }

        System.out.println("Average turn around time: " + totalTurnAround/count);
        System.out.println("Through-put: " + count/100);
        System.out.println();
    }
}
