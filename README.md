<p align="center">
  <img src="logo.png" alt="Logo" width="150">
</p>

# D-SchedAleR

This is a repository for the implementation of a disk scheduling simulator for a requirement in the course CMSC 125 - M.

# System Requirements
* Java Development Kit (JDK): JDK 21 or later
* Operating System: Windows, macOS, or Linux
* IDE (Optional): Visual Studio Code or any Java-compatible IDE

# Installation & Setup
## 1. Clone the Repository
If using Git, clone the repository:<br/>
```
git clone https://github.com/ishomayo/D-SchedAler.git
```
Alternatively, download the source code and extract it to your preferred directory.
## 2. Navigate to the Project Directory
```
cd 
```
## 3. Open a terminal or command prompt in the project directory and run:
```
javac 
```
<br/> After compiling, execute the program: <br/>
```
java 
```

# Formatting the File Input 
## (For User-defined Input from a Text File)
The application expects input files to be formatted as follows:
* Each line contains three space-separated integers in the following order:
  - Length of the Queue
  - Order of Requests Queue
  - R/W Head Starting Position
  - Direction of the Movement
### Example File Format
```
8
98 183 37 122 14 124 65 67
53
LEFT
```
This will read as: <br/>
```
Length of the Queue: 8
Order of Requests Queue: 98 183 37 122 14 124 65 67
R/W Head Starting Position: 53
Direction of the Movement: LEFT
```
_**Note: A sample .txt file is given as example named "sample_format_file.txt"**_
# Features
* **Graphical Interface**: Intuitive UI using Java Swing.
* **Disk Scheduling Algorithms**: Simulates FCFS, SSTF, SCAN, C-SCAN, LOOK, C-LOOK.
* **Interactive Selection**: Users can navigate between different screens to choose and simulate algorithms.

# Snapshots
* Lobby<br/><br/>![image](https://github.com/user-attachments/assets/477f6874-55f9-4d28-b639-2ebd1d889921)<br/><br/>
* Help<br/><br/>![image](https://github.com/user-attachments/assets/609cff18-20af-4563-845d-2c047113d9d4)<br/><br/>
* Data Generation<br/><br/> ![image](https://github.com/user-attachments/assets/3de73b04-81b5-4e3f-a064-04f48c23d9f2)<br/><br/>![image](https://github.com/user-attachments/assets/db2b460c-c120-4adf-9390-ee4e7596b2fd)<br/><br/>![image](https://github.com/user-attachments/assets/dc367280-aebf-4c6f-a272-f415ea887401)<br/><br/>
* Algorithm Selection Screen<br/><br/>![image](https://github.com/user-attachments/assets/06bda6f6-8684-41b2-b3c4-1a1a91eaa0bd)<br/><br/>
* Simulation Screen<br/><br/>![image](https://github.com/user-attachments/assets/4030727a-c0c6-4ef0-bbc1-c4e3c967c568)<br/><br/>![image](https://github.com/user-attachments/assets/915d3c1c-ee82-41a9-bb7c-10be29b73b89)<br/><br/>![image](https://github.com/user-attachments/assets/399c0931-87fb-4b6d-9369-91d64c139972)<br/><br/>![image](https://github.com/user-attachments/assets/a52671bb-0241-47fa-a75a-8a3fe0e57f19)
* Saved PDF File<br/><br/>![image](https://github.com/user-attachments/assets/976fb9bd-80b5-4e5e-ba8c-33312f6255a9)<br/><br/>
* Saved PNG File<br/><br/>![image](https://github.com/user-attachments/assets/e8e326a2-6a13-4f59-8c9d-19876d3b7252)<br/><br/>


   









