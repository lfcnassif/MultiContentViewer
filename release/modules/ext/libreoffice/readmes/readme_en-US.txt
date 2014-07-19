
======================================================================
LibreOffice 4.0 ReadMe
======================================================================


For latest updates to this readme file, see http://www.libreoffice.org/welcome/readme.html

This file contains important information about the LibreOffice software. You are recommended to read this information very carefully before starting installation.

The LibreOffice community is responsible for the development of this product, and invites you to consider participating as a community member. If you are a new user, you can visit the LibreOffice site, where you will find lots of information about the LibreOffice project and the communities that exist around it. Go to http://www.libreoffice.org/.

Is LibreOffice Really Free for Any User?
----------------------------------------------------------------------

LibreOffice is free for use by everybody. You may take this copy of LibreOffice and install it on as many computers as you like, and use it for any purpose you like (including commercial, government, public administration and educational use). For further details see the license text packaged with this LibreOffice download.

Why is LibreOffice Free for Any User?
----------------------------------------------------------------------

You can use this copy of LibreOffice free of charge because individual contributors and corporate sponsors have designed, developed, tested, translated, documented, supported, marketed, and helped in many other ways to make LibreOffice what it is today - the world's leading Open Source productivity software for home and office.

If you appreciate their efforts, and would like to ensure that LibreOffice continues to be available far into the future, please consider contributing to the project - see http://www.documentfoundation.org/contribution/ for details. Everyone can make a contribution of some kind.

----------------------------------------------------------------------
Notes on Installation
----------------------------------------------------------------------

LibreOffice requires a recent version of Java Runtime Environment (JRE) for full functionality. JRE is not part of the LibreOffice installation package, it should be installed separately.

System Requirements
----------------------------------------------------------------------

* Microsoft Windows XP, Vista, Windows 7, or Windows 8
* Pentium compatible PC (Pentium III or Athlon recommended)
* 256 MB RAM (512 MB RAM recommended)
* Up to 1.5 GB available hard disk space
* 1024x768 resolution (higher resolution recommended), at least 256 colors

Please be aware that administrator rights are needed for the installation process.

Registration of LibreOffice as default application for Microsoft Office formats can be forced or suppressed by using the following command line switches with the installer:

* REGISTER_ALL_MSO_TYPES=1 will force registration of LibreOffice as default application for Microsoft Office formats.
* REGISTER_NO_MSO_TYPES=1 will suppress registration of LibreOffice as default application for Microsoft Office formats.

Please make sure you have enough free memory in the temporary directory on your system, and please ensure that read, write and run access rights have been granted. Close all other programs before starting the installation process.

Installation of LibreOffice on Debian/Ubuntu-based Linux systems
----------------------------------------------------------------------

If you have a previous version of LibreOffice already installed, then you will need to de-install it before proceeding further. For instructions on how to install a language pack (after having installed the US English version of LibreOffice), please read the section below entitled Installing a Language Pack.

When you unpack the downloaded archive, you will see that the contents have been decompressed into a sub-directory. Open a file manager window, and change directory to the one starting with "LibO_", followed by the version number and some platform information.

This directory contains a subdirectory called "DEBS". Change directory to the "DEBS" directory.

Right-click within the directory and choose "Open in Terminal". A terminal window will open. From the command line of the terminal window, enter the following command (you will be prompted to enter your root user's password before the command will execute):

sudo dpkg -i *.deb

The above dpkg command does the first part of the installation process. To complete the process, you also need to install the desktop integration packages. To do this, change directory to the "desktop-integration" directory that is within the "DEBS" directory, using the following command:

cd desktop-integration

Now run the dpkg command again:

sudo dpkg -i *.deb

The installation process is now completed, and you should have icons for all the LibreOffice applications in your desktop's Applications/Office menu.

Installation of LibreOffice on Fedora, Suse, Mandriva and other Linux systems using RPM packages
----------------------------------------------------------------------

If you have a previous version of LibreOffice already installed, then you will need to de-install it before proceeding further. For instructions on how to install a language pack (after having installed the US English version of LibreOffice), please read the section below entitled Installing a Language Pack.

When you unpack the downloaded archive, you will see that the contents have been decompressed into a sub-directory. Open a file manager window, and change directory to the one starting with "LibO_", followed by the version number and some platform information.

This directory contains a subdirectory called "RPMS". Change directory to the "RPMS" directory.

Right-click within the directory and choose "Open in Terminal". A terminal window will open. From the command line of the terminal window, enter the following command (you will be prompted to enter your root user's password before the command will execute):

For Fedora-based systems: su -c 'yum install *.rpm'

For Mandriva-based systems: sudo urpmi *.rpm

For other RPM-based systems (Suse, etc.): rpm -Uvh *.rpm

The above command does the first part of the installation process. To complete the process, you also need to install the desktop integration packages. To do this, change directory to the "desktop-integration" directory that is within the "RPMS" directory, using the following command:

cd desktop-integration

Now run the installation command again:

For Fedora-based systems: su -c 'yum install *freedesktop*.rpm'

For Mandriva-based systems: sudo urpmi *mandriva*.rpm

For SUSE-based systems: rpm -Uvh *suse*.rpm

For other RPM-based systems: rpm -Uvh *freedesktop*.rpm

The installation process is now completed, and you should have icons for all the LibreOffice applications in your desktop's Applications/Office menu.

Notes Concerning Desktop Integration for Linux Distributions Not Covered in the Above Installation Instructions
----------------------------------------------------------------------

It should be easily possible to install LibreOffice on other Linux distributions not specifically covered in these installation instructions. The main aspect for which differences might be encountered is desktop integration.

The desktop-integration directory also contains a package named libreoffice3.3-freedesktop-menus-3.3.1.noarch.rpm (or similar). This is a package for all Linux distributions that support the Freedesktop.org specifications/recommendations (http://en.wikipedia.org/wiki/Freedesktop.org), and is provided for installation on other Linux distributions not covered in the aforementioned instructions.

Installing a Language Pack
----------------------------------------------------------------------

Download the language pack for your desired language and platform. They are available from the same location as the main installation archive. From the Nautilus file manager, extract the downloaded archive into a directory (your desktop, for instance). Ensure that you have exited all LibreOffice applications (including the QuickStarter, if it is started).

Change directory to the directory in which you extracted your downloaded language pack.

Now change directory to the directory that was created during the extraction process. For instance, for the French language pack for a 32-bit Debian/Ubuntu-based system, the directory is named LibO_, plus some version information, plus Linux_x86_langpack-deb_fr.

Now change directory to the directory that contains the packages to install. On Debian/Ubuntu-based systems, the directory will be DEBS. On Fedora, Suse or Mandriva systems, the directory will be RPMS.

From the Nautilus file manager, right-click in the directory and choose the command "Open in terminal". In the terminal window you just opened, execute the command to install the language pack (with all of the commands below, you may be prompted to enter your root user's password):

For Debian/Ubuntu-based systems: sudo dpkg -i *.deb

For Fedora-based systems: su -c 'yum install *.rpm'

For Mandriva-based systems: sudo urpmi *.rpm

For other RPM-using systems (Suse, etc.): rpm -Uvh *.rpm

Now start one of the LibreOffice applications - Writer, for instance. Go to the Tools menu and choose Options. In the Options dialog box, click on "Language Settings" and then click on "Languages". Dropdown the "User interface" list and select the language you just installed. If you want, do the same thing for the "Locale setting", the "Default currency", and the "Default languages for documents".

After adjusting those settings, click on OK. The dialog box will close, and you will see an information message telling you that your changes will only be activated after you exit LibreOffice and start it again (remember to also exit the QuickStarter if it is started).

The next time you start LibreOffice, it will start in the language you just installed.

----------------------------------------------------------------------
Problems During Program Startup
----------------------------------------------------------------------

Difficulties starting LibreOffice (e.g. applications hang) as well as problems with the screen display are often caused by the graphics card driver. If these problems occur, please update your graphics card driver or try using the graphics driver delivered with your operating system. Difficulties displaying 3D objects can often be solved by deactivating the option "Use OpenGL" under 'Tools - Options - LibreOffice - View - 3D view'.

----------------------------------------------------------------------
ALPS/Synaptics notebook touchpads in Windows
----------------------------------------------------------------------

Due to a Windows driver issue, you cannot scroll through LibreOffice documents when you slide your finger across an ALPS/Synaptics touchpad.

To enable touchpad scrolling, add the following lines to the "C:\Program Files\Synaptics\SynTP\SynTPEnh.ini" configuration file, and restart your computer:

[LibreOffice]

FC = "SALFRAME"

SF = 0x10000000

SF |= 0x00004000

The location of the configuration file might vary on different versions of Windows.

----------------------------------------------------------------------
Shortcut Keys
----------------------------------------------------------------------

Only shortcut keys (key combinations) not used by the operating system can be used in LibreOffice. If a key combination in LibreOffice does not work as described in the LibreOffice Help, check if that shortcut is already used by the operating system. To rectify such conflicts, you can change the keys assigned by your operating system. Alternatively, you can change almost any key assignment in LibreOffice. For more information on this topic, refer to the LibreOffice Help or the Help documention of your operating system.

----------------------------------------------------------------------
Problems When Sending Documents as E-mails From LibreOffice
----------------------------------------------------------------------

When sending a document via 'File - Send - Document as E-mail' or 'Document as PDF Attachment' problems might occur (program crashes or hangs). This is due to the Windows system file "Mapi" (Messaging Application Programming Interface) which causes problems in some file versions. Unfortunately, the problem cannot be narrowed down to a certain version number. For more information visit http://www.microsoft.com to search the Microsoft Knowledge Base for "mapi dll".

----------------------------------------------------------------------
Important Accessibility Notes
----------------------------------------------------------------------

For more information on the accessibility features in LibreOffice, see http://www.libreoffice.org/accessibility/

----------------------------------------------------------------------
User Support
----------------------------------------------------------------------

The main support page http://www.libreoffice.org/support/ offers various possibilities for help with LibreOffice. Your question may have already been answered - check the Community Forum at http://www.documentfoundation.org/nabble/ or search the archives of the 'users@libreoffice.org' mailing list at http://www.libreoffice.org/lists/users/. Alternatively, you can send in your questions to users@libreoffice.org. If you like to subscribe to the list (to get email responses), send an empty mail to: users+subscribe@libreoffice.org.

Also check the FAQ section at http://www.libreoffice.org/faq/.

----------------------------------------------------------------------
Reporting Bugs & Issues
----------------------------------------------------------------------

Our system for reporting, tracking and solving bugs is currently BugZilla, kindly hosted at https://bugs.freedesktop.org/. We encourage all users to feel entitled and welcome to report bugs that may arise on your particular platform. Energetic reporting of bugs is one of the most important contributions that the user community can make to the ongoing development and improvement of LibreOffice.

----------------------------------------------------------------------
Getting Involved
----------------------------------------------------------------------

The LibreOffice Community would very much benefit from your active participation in the development of this important open source project.

As a user, you are already a valuable part of the suite's development process and we would like to encourage you to take an even more active role with a view to being a long-term contributor to the community. Please join and check out the contributing page at http://www.libreoffice.org/contribution/

How to Start
----------------------------------------------------------------------

The best way to start contributing is to subscribe to one or more of the mailing lists, lurk for a while, and gradually use the mail archives to familiarize yourself with many of the topics covered since the LibreOffice source code was released back in October 2000. When you're comfortable, all you need to do is send an email self-introduction and jump right in. If you are familiar with Open Source Projects, check out our To-Dos list and see if there is anything you would like to help with at http://www.libreoffice.org/develop/.

Subscribe
----------------------------------------------------------------------

Here are a few of the mailing lists to which you can subscribe at http://www.libreoffice.org/contribution/

* News: announce@documentfoundation.org *recommended to all users* (light traffic)
* Main user list: users@global.libreoffice.org *easy way to lurk on discussions* (heavy traffic)
* Marketing project: marketing@global.libreoffice.org *beyond development* (getting heavy)
* General developer list: libreoffice@lists.freedesktop.org (heavy traffic)

Joining one or more Projects
----------------------------------------------------------------------

You can make major contributions to this important open source project even if you have limited software design or coding experience. Yes, you!

We hope you enjoy working with the new LibreOffice 4.0 and will join us online.

The LibreOffice Community

----------------------------------------------------------------------
Used / Modified Source Code
----------------------------------------------------------------------

Portions Copyright 1998, 1999 James Clark. Portions Copyright 1996, 1998 Netscape Communications Corporation.
