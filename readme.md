# Virtual Memory Management

#### Operating Systems
#### CIS 657
#### Project 2

## Program Arguments:
Unzip the file Project2 into your workspace and use the option new and project from existing workspace to run in IntelliJ
for eclipse
For running this project, Create a new project from an existing source and uncheck the "use default location" box and find the directory where your project is.


## My program arguements used to run the program
IDE Used:IntelliJ.Eclipse
-x /Users/suruchisingh/Desktop/POS/Project2/project2/test/matmult,/Users/suruchisingh/Desktop/POS/Project2/project2/test/sort
Output of the program can be found in output.txt
Runs for two files when separated by comma 


## Workings of the Project

Implemented the following in this project:
1.Task, 2.1 Swap Space, 2.2 Page Fault Handling, 2.3 Page Replacement Algorithm

3. Dynamic Linkage has been explained below

Reduced Ram Size to 32 in Machine.java
create a swapspace file which is an openfile object  and a counter to index this file
to record the page number we also need to make entry in translation table by setting physical page =-1

The swapCounter value is then assigned to translationMapping as well as memoryMapping
we then write to swapspace file and increment the counter.
Once the pageFault Exception occurs we do the following


Implemented Page Replacement Algorithm FIFO
find page number from physical address
We check if the RAM is full if yes then we need to remove the page using the Scheduler

Now check if the page is dirty if yes mark it as non dirty and create a buffer space to copy the contents
the page information is now copied from RAM into buffer and back to swapspace
Thr transaltional entry for the evicted page is now updated
to count the number of page faults we use Statistics.numPageFaults and increment on every page fault
By reading the value from the register 39 which contains the failing virtual address on an exception
then we read from the swapspace file and write to Main Memory using arrayCopy

We then check if the scheduler is empty or not
if it is empty then we add it to the page table else add it to the last of the list

By implementing disk clean up of the code we can see that the number of page faults being reduced by a factor of nearly 1000
For disk clean up, we have cleared the disk of the pages and also zeroed out all the Main Memory and the Scheduler (FIFO)



***Dynamic Linkage***

Dynamic Linking
----------------
There usually is a common library package that is shared by all the processes and only a single copy of this is kept in the memory.
We don’t know where this library is loaded until runtime and all the references are resolved dynamically or during runtime.
When a page is loaded, we statically relocate another page from the page frame.
However, for dynamic linking, we make changes to the Memory Management Unit (MMU.java), that changes the addresses dynamically for every memory reference.
During every memory reference, each virtual address is translated in MMU to a physical address.

We can create an array (jump table) where the entries correspond to machine instructions that mention a jump.
Initially, the array contents are initialized to zero.
There should be a shared library used and for every page, there will be one entry in this array that will jump to the beginning of this function.
If the code being linked is a shared library, the linker doesn’t have the code in the final program as it creates a table.
This table contains columns for all the pages that are used from the library.
For a replacement of these pages contained in the shared library, the linker puts the new page’s address in the jump table entry.
When the system call is invoked, the jump table entry will be called by the referencing caller. This is then redirected to the actual function.
When the program starts up, the shared library is loaded into the memory and the jump table addresses are adjusted to reflect the location where it has been loaded.
It should invoke all the mapping functionalities to load the shared library into memory.The jump table is filled with
the accurate addresses of each page in the shared library.
