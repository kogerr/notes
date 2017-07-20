# Linux

### Linux or GNU/Linux
- Linux is only the kernel, not the applications
- most CL utiities by Free Software Foundation (cp, mv lsof, dd...)

## Overview
### Various Parts of an Operating System
kernel & system programs, application programs

kernel: 
- keeps track of files on the disk
- starts programs
- runs them concurrently
- assigns memory and other resources to various processes
- receives packets from and sends packets to the network
- etc.
- prevents accessing the hardware directly, use the tools it provides
- tools: *system calls*

system programs:
- use tools of the kernel
- implement the various services required from an operating system
- necessary for the system, e.g.: `mount`
  
### Parts of the Kernel
Memory management:
- assigning memory areas 
- swap space areas to processes, parts of the kernel
- buffer cache

Process management:
- creates processes
- implements multitasking by switching the active process on the processor

lowest level
- hardware device drivers
- classes of drivers with the same interface

software services
- can be abstracted into classes
- e.g. network protocols are abstracted into one API: BSD socket library
- e.g. Virtual Filesystem (VFS) abstracts filesystem operations

### Major services in a UNIX system
#### `init`
- started as the first process of every UNIX system
- last thing the kernel does when it boots
- startup chores (checking & mounting filesystems, starting daemons, etc).

Run levels
- single user mode: no one can log in and root uses a shell at the console
- multiuser mode is usual

Linux allows for up to 10 runlevels
- 0 system halt
- 1 single user mode
- 3 multiuser
- 5 same as 3 except that a GUI gets started also
- 6 system reboot
- `/etc/inittab`

after boot:
- `getty` is working (to allow users to log in)
- to adopt orphan processes

on shutdown:
- killing all other processes
- unmounting all filesystems
- stopping the processor
- etc.

#### Logins from terminals
`getty` provides logins
- from terminals (via serial lines)
- the console (when not running X)

`init` starts a separate instance of `getty` for each terminal

- `getty` reads the username and runs the loginprogram
- loginprogram reads the password
- if correct, login runs the shell
- `init` starts a new instance of `getty` if:
  - shell terminates, i.e., the user logs out
  - when login terminated because the username and password didn't match

The kernel has no notion of logins, this is all handled by the system
programs.

#### Syslog
- error, warning, and other messages from kernel & system programs
- `syslog` writes these to a file
- can sort to different files according to writer / degree of importance
- e.g. kernel messages to different files

#### Periodic command execution: `cron` and `at`
e.g. clean temporary files (`/tmp` and `/var/tmp`) from old files

`cron` service
- Each user can have a `crontab` file
- lists commands to execute & times they should be executed
- cron daemon takes care of starting the commands when specified

`at` service
- similar
- command is executed at the given time
- but it is not repeated

#### GUI
UNIX and Linux don't incorporate the user interface into the kernel
- X Window System (not uiser interface)
- i.e., tools with which a graphical user interface can be implemented
- popular window managers: fvwm, icewm, blackbox, and windowmaker
- two popular desktop managers: KDE and Gnome

#### Networking
- basic services: filesystems, printing, backups, etc
- benefits of microcomputing & distributed computing:
  - lower costs 
  - better fault tolerance

#### Network Logins
different than normal
- separate virtual network connection for each person logging in
- not possible to run a separate `getty` for each possible virtual connection
- different ways: telnet and ssh (more secure) major in TCP/IP
- a single daemon listens for all incoming login attempts
- When it notices one: new instance of itself for that single attempt
- original instance continues to listen
- new instance works similarly to `getty`

#### Network file systems
Network File System (NFS) / Common Internet File System (CIFS)
- NFS - Unix based
- for CIFS: samba
- fools the program to think that files are actually on the computer

#### Mail
- an electronic letter is stored in a file using a special format
- special mail programs are used to send and read the letters
- When someone sends mail
  - the mail program locates the receiver's mailbox
  - appends the letter to the mailbox file
  - If the receiver's mailbox is in another machine, the letter is sent to the
other machine, which delivers it to the mailbox as it best sees fit

The mail system consists of many programs
- Mail Transfer Agent (MTA) - e.g., `sendmail` or `postfix`
- mail user agent (MUA) - e.g., `pine` , or `evolution`
- mailboxes are usually stored in `/var/spool/mail` until MUA retrieves them

#### Printing
*print queue*
- print queue software *spools* the printouts on disk

#### Filesystem Layout
The filesystem is divided into many parts

## Overview of the Directory Tree
### Background
Filesystems Hierarchy Standard (FHS)
- easier to write or port software for Linux, and to administer Linux machines
- full directory tree is breakable into smaller parts
- each capable of being on its own disk or partition
- major parts: root (`/`), `/usr`, `/var`, `/home`

##### root
- specific for each machine
- generally stored on a local disk (could be a ramdisk or network drive as well)
- files that are necessary for booting
- mounting other filesystems
- sufficient for the single user state
- tools for fixing & recovery from backups
- should generally be small
- generally doesn't contain any files, only in subdirectories


##### `/usr`
- all commands, libraries, manual pages, and other unchanging files needed
- not specific
- not modified during normal use
- this allows the files to be shared - network mounted
- could be mounted read-only

##### `/var`
files that change
- spool directories (for mail, news, printers, etc)
- log files
- formatted manual pages
- temporary files

`/var` was below `/usr`, but could not mount `/usr` read-only

##### `/home`
- data
- can be broken across several filesystems (e.g. /home/students and /home/staff)

##### `/dev`
- `/dev/MAKEDEV` / `/dev/MAKEDEV.local`

##### `/usr`
- all programs are installed there
- files in `/usr` come from a Linux distribution
- `/usr/local`: locally installed programs, etc

##### `/proc`
illusionary, he kernel creates it in memory
- provides information about the system (originally about processes)

## Filesystems
- the way the files are organized on the disk
- also refers to a partition or disk

making a filesystem:
- initialize
- write bookkeeping data structures to the disk

Central Concepts
- superblock: ~ as a whole, such as size
- inode:
  - all information about a file, except its name (name in directory)
  - directory entry: filename and the number of the inode
  - numbers of data blocks
- data block: store the data in the file
- directory block
- indirection block: dynamically allocated blocks are indirect blocks (?)

hole in a file
- `lseek()`
- pretends that at a particular place in the file there is just zero bytes
- no actual disk sectors are reserved
- use a bit less disk space

### Filesystems Galore
#### Creating a filesystem
- `mkfs -t fstype`
- mkfs just frontend, selects proper program
- `-c` Search for bad blocks and initialize the bad block list accordingly.
- `-l filename` Read the initial bad block list from the name file.
- `mkfs.ext3 -b` specify block size
- `mkfs.ext2 -c` could be done by:

```bash
$ fdformat -n /dev/fd0H1440
$ badblocks /dev/fd0H1440 1440 $>$ bad-blocks
$ mkfs.ext2 -l bad-blocks /dev/fd0H1440
```

#### Filesystem Block Size
Larger block sizes:
  - improve disk I/O performance (no need to search next block)
  - smaller files waste a lot of disk space

worlds printed material is equal to 5 Exabytes

#### Mounting and Unmounting
Since all files in UNIX are in a single directory tree, the mount operation will
make it look like the contents of the new filesystem are the contents of an
existing subdirectory in some already mounted filesystem.

```bash
$ mount /dev/hda2 /home
$ mount /dev/hda3 /usr
```

- *mount point*: the mounted-on directory. here `/home` and `/usr`
- `mount` tries to guess the type of the filesystem
- or `$ mount -t msdos /dev/fd0 /floppy`
- `-r` switch: read-only mount
- root filesystem is magically mounted at boot time
- name of root is either compiled into the kernel, or set using LILO or rdev
- root filesystem first mounted read-only, then `fcsk`, then re-mount
- `/etc/fstab` file: filesystems mounted automatically at boot time
- `$ umount /dev/hda2` - neccessary because of disk caching
- allow users to mount floppies

#### Checking filesystem integrity with `fsck`
repairs minor problems, alerts if there are unrepairable problems

problems caused by
- power failures
- failing hardware
- or operator errors, e.g.: by not shutting down the system properly

- `/etc/fastboot` - no `fsck`
- ext2 marker: was it unmounted properly

#### Checking for Disk Errors with `badblocks`
check for bad blocks, feed to `fcsk`

#### Fighting Fragmentation
files can't always be written in consecutive blocks

#### Other tools for all filesystems
- `df` shows the free disk space on one or more filesystems
- `du` shows how much disk space a directory and all its files contain
- `sync` forces unwritten blocks in buffer cache to be written to disk
  - daemon process `update` does this automatically

The `man` is your very best friend in Linux. Its cousin `apropos` is also very
useful when you don't know what the name of the command you want is.

#### Other tools for the ext2/ext3 filesystem
- `mke2fs` filesystem creator
- `e2fsck` checker
- `tune2fs` adjusts filesystem parameters
  - maximal mount count before `e2fsck`
  - maximal time between checks
  - Number of blocks reserved for root
- `dumpe2fs` shows information about an ext2 or ext3 filesystem
- `debugfs` is a filesystem debugger. what `fsck` can't fix
- `dump` and `restore` can be used to back up an ext2 filesystem

## System Monitoring
### System Resources
- low == lot of problems
- can be taken up by:
  - individual users
  - services your system may host such as email or web pages
- determine whether
  - system upgrades are needed
  - some services need to be moved to another machine

#### `top`
`top` displays a continually updating report of system resource usage
- `i` display idle processes on/off
- `M` sort by memory usage
- `S`sort by how long they processes have been running
- `P` sort by CPU usage
- `u` to view processes owned by a specific user
- `k` to kill processes
- `r` to renice them

#### `iostat`
- current CPU load average
- disk I/O information

#### `ps`
list of processes currently running
- `ps -ef` (partial output)

#### `vmstat`
system processes statistics
- memory
- swap
- I/O
- CPU

statistics from the last time the command was run

#### `lsof`
- print out a list of every file that is in use
- if you wish to unmount a filesystem, but you are being told that it is in use
- `lsof -p -processid-`

### Filesystem Usage
#### `df`
disk usage
- `-h` human readable (K, Megs, or Gigs)
- `-B` block size

#### `du`
find out where that data is
- Unless you specify a filename du will act recursively
- `-h` human readable
- `-s` summary of directory

### Monitoring Users
maybe for resources

#### `who`
- who is logged on the system
- what port or terminal they are logged on at

#### `ps`
what users are doing: `ps -u name` (dies it still work?)

#### `w`
- who is on the system
- the commands they are running

## Linux File Permissions
Permission Groups
- owner
- group
- all users

#### Permission Types
- read - read the contents of the file
- write - modify a file or directory
- execute - execute a file or view the contents of a directory

#### Viewing the Permissions
- by GUI File Manager
- by `ls -l`

`_rwxrwxrwx 1 owner:group`
- first character: special permission flag
- owner permissions
- group permissions
- all users permissions
- number of hardlinks to the file
- Owner and Group assignment

#### Modifying the Permissions
`chmod`
- explicitly or using a binary reference

#### Explicitly Defining Permissions
Permission Groups
- `u` - Owner
- `g` - Group
- `o` - Others
- `a` - All users

`+` / `-` - add or remove the specific permissions

Permission Types
- `r` - Read
- `w` - Write
- `x` - Execute

e.g.: `chmod a-rw file1` to remove all users read and write permissions

#### Using Binary References to Set permissions
e.g. `chmod 640 file1`
- owner: read and write
- group: read
- all other users: no rights

----|---
`r` | 4
`w` | 2
`x` | 1

#### Owners and Groups
`chown owner:group filename`

#### Advanced Permissions
- `_` - no special permissions
- `d` - directory
- `l` - The file or directory is a symbolic link
- `s` - setuid/setgid permissions (in read portion of owner/group)
- `t` - sticky bit permissions (in read portion of all users)

#### Setuid/Setgid Special Permissions
tells the system to run an executable as the owner with the owner's permissions
- `chmod g+s file2.sh`

#### Sticky Bit Special Permissions
only file owner can rename or delete the file
- useful in shared environment
- `chmod +t dir1`

#### When Permissions Are Important
home directories
- only for user
- drwx______ (700)
- chmod 700 /home/user1

bootloader configuration files
- only root: 700

system and daemon configuration files
- users only read not write: 644

firewall scripts
- firewall script is run by the root user automatically on boot: 700

## Managing User Accounts
account
- name
- files
- resources
- information

### Creating a user
*user id* or *uid*: unique integer
- separate database outside the kernel assigns username to uid

To create a user
- add information about the user to the user database
- create a home directory
- maybe educate the user, and set up a suitable initial environment
- either through GUI or `adduser` and `useradd`

#### `/etc/passwd` and other informative files
fields:
- Username
- (Previously this was where the user's password was stored)
- Numeric user id
- Numeric group id
- Full name or other description of account
- Home directory
- Login shell (program to run at login)

*shadow passwords*: the encrypted password is stored in a separate file,
`/etc/shadow`, which only root can read.
setuid programs can access the shadow password file

#### Picking numeric user and group ids
NFS: you need to have the same uid and gid on all systems

#### Initial environment: `/etc/skel`
e.g. `/etc/skel/.profile` sets the EDITOR environment
- keep `/etc/skel` small
- put global configuration into global files, such as `/etc/profile`

#### Creating a user by hand
- Edit `/etc/passwd` with `vipw` (locks the file)
- Set password field '*' - impossible to log in
- Edit `/etc/group` with `vigr`, if you need to create a new group as well
- Create the home directory of the user with `mkdir`
- Copy the files from `/etc/skel` to the new home directory
- Fix ownerships and permissions with `chown` and `chmod`
```bash
cd /home/newusername
chown -R username.group .
chmod -R go=u,go-w .
chmod go= .
```
- Set the password with `passwd`

### Changing User Properties
- `chfn` Change the full name field.
- `chsh` Change the login shell.
- `passwd` Change the password.

disable these commands (with `chmod`) for normal users

### Removing a User
remove
- files
- mailboxes
- mail aliases
- print jobs
- `cron` and `at` jobs
- all other references to the user
- remove relevant lines from `/etc/passwd` and `/etc/group`
- `find / -user username` find user's file outside home directory

or look for `deluser` or `userdel`

### Disabling a user temporarily
change its shell into a special program that just prints a message

```bash
#!/usr/bin/tail +2
This account has been closed due to a security breach.
Please call 555-1234 and wait for the men in black to arrive.
```

```bash
# chsh -s 
/usr/local/lib/no-login/security billg
# su - tester
```

### Important Linux Commands
#### File Commands
##### File Administration

command					| ``
--------------------------------------- | -------------
`ls [option(s)] [file(s)]`		| **list**
`ls -l`					| detailed
`ls -a`					| hidden files
`cp [option(s)] sourcefile targetfile`	| **copy**
`cp -i`					| Waits for confirmation
`cp -r`					| recursively (subdirectories too)
`mv [option(s)] sourcefile targetfile`	| **move**
`mv -b`					| backup copy before moving
`mv -i`					| confirmation
`rm [option(s)] file(s)`		| **remove**
`rm -r`					| 
`rm -i`					| 
`ln [option(s)] sourcefile targetfile`	| **internal link**
`ln -s`					| symbolic link
`cd [options(s)] [directory]`		| change directory
`mkdir [option(s)] directoryname`	| create directory
`rmdir [option(s)] directoryname`	| deletes directory if empty
`chown [option(s)] username.group file(s)`	| Transfer ownership
`chown -R`				| recursively
`chgrp [option(s)] groupname file(s)`	| Transfere group ownership
`chmod [options] mode file(s)`		| Change access permissions
`gzip [parameters] file(s)`		| **compress to `.gz`**
`gzip -d`				| decompress
`tar options archive file(s)`		| **put files into a `.tar` archive**
`tar -f`				| to a file and not to the screen
`tar -c`				| new tar archive
`tar -r`				| to an existing archive
`tar -t`				| Output the contents of an archive
`tar -u`				| Add files, if not present
`tar -x`				| Unpack / extract
`tar -z`				| Pack resulting archive with gzip
`tar -j`				| Compress resulting archive with bzip2
`tar -v`				| List files processed
`locate pattern(s)`			| find directory of a file 
`updatedb [options(s)]`			| update locate database
`find [option(s)]`			| search in a given directory

##### Commands to Access File Contents

command 				| ``
--------------------------------------- | -------------
`cat [option(s)] file(s)`		| display the contents
`cat -n`				| Numbers the output on the left margin
`less [option(s)] file(s)`		| 
`grep [option(s)] searchstring files`	| finds in the specified file(s)
`grep -i`				| Ignore case
`grep -l`				| Only display names of the files
`grep -n`				| numbers of the lines
`grep -L`				| files in which not found
`diff [option(s)] file1 file2`		| compares the contents of two files
`diff -q`				| whether the two given files differ

##### File Systems

command 				| ``
--------------------------------------- | -------------
`mount [option(s)] [<device>] mountpoint` |  mount any data media
`mount -r`				| mount read-only
`mount -t filesystem`			| Specifies the file system
`umount [option(s)] mountpoint`		| 

#### System Commands
##### System Information

command 				| ``
--------------------------------------- | -------------
``				| 
``				| 

