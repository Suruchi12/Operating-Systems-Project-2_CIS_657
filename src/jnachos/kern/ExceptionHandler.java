/**
 * Copyright (c) 1992-1993 The Regents of the University of California.
 * All rights reserved.  See copyright.h for copyright notice and limitation 
 * of liability and disclaimer of warranty provisions.
 *
 *  Created by Patrick McSweeney on 12/13/08.
 *
 */
package jnachos.kern;

import jnachos.machine.*;

/**
 * The ExceptionHanlder class handles all exceptions raised by the simulated
 * machine. This class is abstract and should not be instantiated.
 */
public abstract class ExceptionHandler {

	/**
	 * This class does all of the work for handling exceptions raised by the
	 * simulated machine. This is the only funciton in this class.
	 *
	 * @param pException
	 *            The type of exception that was raised.
	//	 * @see ExceptionType.java
	 */
	public static void handleException(ExceptionType pException) {
		switch (pException) {
			// If this type was a system call
			case SyscallException:

				// Get what type of system call was made
				int type = Machine.readRegister(2);
				// Invoke the System call handler
				SystemCallHandler.handleSystemCall(type);
				break;

			case PageFaultException:
				//find page number from physical address
				int physicalPageNumber = AddrSpace.mFreeMap.find(); //find() function from BitMap
				//find() function - find and used to mark an unused bit, physical page number of address space
				//failing virtual address on an exception
				int virtualAddressFault = Machine.readRegister(39);
				callpagefault(physicalPageNumber,virtualAddressFault);
				break;

			// All other exceptions shut down for now
			default:
				System.exit(0);
				break;
		}
	}

	private static void callpagefault(int physicalPageNumber, int virtualAddressFault) {
		try {


			if (physicalPageNumber < 0) {
				TranslationEntry extractPage = JNachos.SchedulingFIFO.removeFirst(); //removing page from Scheduler
				if (extractPage.dirty)  //checks if page is dirty
				{
					extractPage.dirty = false;    //mark the page as non dirty
					byte[] tempStore = new byte[Machine.PageSize];  //create buffer
					//copy page info from RAM into buffer
					System.arraycopy(Machine.mMainMemory, extractPage.physicalPage * Machine.PageSize, tempStore, 0, Machine.PageSize);
					//copy info from buffer to SwapSpace
					JNachos.SwapSpaceFile.writeAt(tempStore, Machine.PageSize, extractPage.translationalMapping * Machine.PageSize);
				}

				//update the translational entry for that page
				physicalPageNumber = extractPage.physicalPage;
				extractPage.physicalPage = -1;
				extractPage.valid = false;
				extractPage.use = false;
			}

			//counting page faults
			Statistics.numPageFaults = Statistics.numPageFaults + 1;

			//create buffer
			byte[] tempStore = new byte[Machine.PageSize];
			//buffer into Memory
			int faultVPageNumber = (virtualAddressFault / Machine.PageSize);  // returns (virtualAddressFault/Machine.PageSize)
			displaystats(Statistics.numPageFaults, faultVPageNumber, tempStore);

			//put info read from SwapSpace to Main Memory
			System.arraycopy(tempStore, 0, Machine.mMainMemory, physicalPageNumber * Machine.PageSize, Machine.PageSize);


			JNachos.getCurrentProcess().getSpace().mPageTable[faultVPageNumber].physicalPage = physicalPageNumber;
			JNachos.getCurrentProcess().getSpace().mPageTable[faultVPageNumber].valid = true;
			JNachos.getCurrentProcess().getSpace().mPageTable[faultVPageNumber].use = true;

			if (JNachos.SchedulingFIFO.isEmpty() == true) {
				JNachos.SchedulingFIFO.addFirst(JNachos.getCurrentProcess().getSpace().mPageTable[faultVPageNumber]);
			}//adding page to the Scheduler again depending on the status of the Linked List
			//JNachos.SchedulingFIFO.addFirst(mPageTable);
			else
				JNachos.SchedulingFIFO.addLast(JNachos.getCurrentProcess().getSpace().mPageTable[faultVPageNumber]);
		}
		catch(Exception e)
		{
			System.out.println("Exception caught" + e);
		}


	}

	private static void displaystats(int pageFaults, int numPageFaults, byte[] tempStore) {

		System.out.println("---------------------------------------------------------------");
		System.out.println("***Page fault occurs!***");
		System.out.println("Virtual Page Number of Page Fault : " + numPageFaults);
		System.out.println("Virtual Address of Page Fault : " + JNachos.getCurrentProcess().getSpace().memoryMapping[numPageFaults]*Machine.PageSize);
		JNachos.SwapSpaceFile.readAt(tempStore, Machine.PageSize, JNachos.getCurrentProcess().getSpace().memoryMapping[numPageFaults]*Machine.PageSize);

		System.out.println("Number of Page Faults : " + Statistics.numPageFaults);
		System.out.println("---------------------------------------------------------------");


	}
}
