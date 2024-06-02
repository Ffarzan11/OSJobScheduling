import java.util.*;

public class SRT {

    protected PriorityQueue<JobObject> jobs = new PriorityQueue<>(new FIFOComparator());
    protected PriorityQueue<JobObject> wait = new PriorityQueue<>(new SRTComparator());
    protected Deque<JobObject> running = new ArrayDeque<>();
    protected PriorityQueue<JobObject> complete = new PriorityQueue<>(new FIFOComparator());
    protected double totalTurnAround = 0;
    protected double count = 0;
    protected int lastExit = 0;


    //default constructor
    public SRT() {

    }

    /**
     *
     * @param jobs the array of jobs which will be put in a priority queue
     */
    public SRT(JobObject[] jobs) {
        JobObject[] jobsSRT = new JobObject[jobs.length];
        for(int i = 0; i < jobs.length; i++) {
            jobsSRT[i] = new JobObject(jobs[i].arrTime, jobs[i].cpuBurst, jobs[i].priority, jobs[i].remainingTime);
        }
        this.jobs.addAll(Arrays.asList(jobsSRT));
    }


    public void setJobTimes(){
        //int completed = 0;
        int timer = 0;

        // run till there is no more job left in the job stack
        while(!jobs.isEmpty()) {
            JobObject current = null;
            JobObject next = null;

            if(timer == jobs.peek().arrTime) {
                current = jobs.poll();
                current.remainingTimes.add(current.remainingTime);
                running.push(current);
               // add to running only if it's time arrives
            }

            //if there is more than one job, then next job is the job right after current in the stack
            if(!jobs.isEmpty()) {
                next = jobs.peek();
            }
            //while something is running continue the loop
            while (!running.isEmpty()|| (!jobs.isEmpty()  && timer == jobs.peek().arrTime) ) {

                if(wait.isEmpty() && running.isEmpty()) {
                    current = jobs.poll();
                    running.push(current);
                }

                while (next != null && timer == next.arrTime ) {
                    if( next.remainingTime < current.remainingTime) {
                        JobObject temp = current;
                        //current.exitTime = current.cpuBurst - current.remainingTime;
                        running.pop(); // removing the current job from run stack
                        current = jobs.poll(); // set the newly arrived job to current
                        running.push(current); // adding the new job to the run stack
                        wait.add(temp); // adding the old current job to wait list
                        if (!jobs.isEmpty()) { // if there is more job available then set the next job to the job that arrives next
                            next = jobs.peek(); // updating the next job
                        } else {
                            next = null;
                        }
                        temp.remainingTimes.add(temp.remainingTime);
                        current.remainingTimes.add(current.remainingTime);
                    }
                    else {
                        JobObject waitJob = jobs.poll();
                        wait.add(waitJob); // add the newly arrived job to waitlist
                        waitJob.remainingTimes.add(waitJob.remainingTime);
                        if(!jobs.isEmpty()) {
                            next = jobs.peek(); // updating the next job
                        }
                        else{
                            next = null; //no more job left
                        }
                    }
                }
                //check if the current running job is completed or not
                if (current.remainingTime == 0) {
                    current.remainingTimes.add(current.remainingTime);
                    current.exitTime = timer;
                    lastExit = current.exitTime;
                    current.turnAroundTime = current.exitTime - current.arrTime;
                    totalTurnAround += current.turnAroundTime;
                    //prevExit = current.exitTime;
                    complete.add(current); // add to completed stack
                    running.pop();
                    if(timer <= 100) {
                        count+=1;
                    }
                    //if job and wait queue is non empty and the next job in job stack has arrived
                    if (!jobs.isEmpty() && !wait.isEmpty() && jobs.peek().arrTime == current.exitTime) {
                        // if the newly arrived job takes less time to execute
                        if (jobs.peek().remainingTime < wait.peek().remainingTime) {
                            current = jobs.poll(); // set the current job to that and push it to running stack
                            current.remainingTimes.add(current.remainingTime);
                            running.push(current);

                            //if there is more job left in the stack, set the next to it
                        } else {
                            current = wait.poll(); //otherwise set current job from wait queue and push to running stack
                            running.push(current);
                            JobObject waitJob =jobs.poll();
                            wait.add(waitJob);
                            waitJob.remainingTimes.add(waitJob.remainingTime);

                        }
                        if(!jobs.isEmpty()) {
                            next = jobs.peek();
                        }
                        else {
                            next = null;
                        }
                    } else if (!jobs.isEmpty() && wait.isEmpty() && jobs.peek().arrTime == current.exitTime) {
                        //if there is no job waiting and new job has arrived to job stack then push the current job from job stack
                        current = jobs.poll();
                        running.push(current);
                        current.remainingTimes.add(current.remainingTime);
                    } else if (!wait.isEmpty()) {
                        // if a job is waiting push the least remaining time job to running stack
                        current = wait.poll();
                        running.push(current);
                    }


                }

                /*
                    To check if a job has arrived when a current job is running
                    If there is more jobs left in the stack, and it has arrived and it takes less time than current job
                 */

                timer += 1;
                current.remainingTime -= 1;

            }
            timer += 1;

        }

    }

    public void print() {
        System.out.println("SRTF Scheduling");
        System.out.println("| Arrival time\t| CPU Burst\t| Exit\t| Turn Around Time\t| Remaining Time\t");
        while (!complete.isEmpty()) {
            JobObject job = complete.poll();
            System.out.print("|\t\t"+job.arrTime + "\t\t|\t");
            System.out.print(job.cpuBurst + "\t\t|\t");
            System.out.print(job.exitTime + "\t|\t");
            System.out.print("\t"+ job.turnAroundTime + "\t\t\t|\t");
            for(int time: job.remainingTimes) {
                System.out.print(time + " ");
            }
            System.out.println("\t\t\t");
        }

        System.out.println("Average turn around time: " + totalTurnAround/count);
        System.out.println("Through-put: " + count/100);
        System.out.println();
    }
}
