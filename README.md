# Memo notes to organize your thoughts and todo items
# With rich-text editor using SpringBoot, Thymeleaf, JPA, RDBMS, Quilljs and Export to PDF

----

### Building and running:
mvn clean package

java -jar target/notes-0.0.1-SNAPSHOT.jar


The app can be accessed at localhost:8081, with the default user id "user" and password "pass". The app creates SQLite db notesdb.sqlite in user's home directory if it doesn't exists there.

1. Main class is NotesApplication

2. Spring security is set up to use in-memory authentication for simplicity since this is a demo only. User id is XXX, password is PPP

3. Pick a profile to run:
-Dspring.profiles.active=windows
or -Dspring.profiles.active=linux

4. You can set a session timeout by passing a VM argument: 
e.g. -Dserver.servlet.session.timeout=30m





----

Features:
- Notes can be created, edited, deleted, exported to PDF
- Notes persisted to the DB, in particular to a SQLite db specified in the corresponding profile. Switching to another RDBMS is easy, Postgres example is included in the properties
- Notes belong to a single user, and the other users cannot see or edit other users' notes, unless the owner marks their note as 'shared'
- Notes are listed with a short preview on the home page
- Notes can be assigned to a category. Category name can be chosen via auto-completion based on the existing notes' categories
- UI is implemented with pagination
- Search function is performed based on the note content and/or category
- Notes can be edited by clicking on the "Last edited" link or by viewing the note and clicking Edit. Saving via Ctrl-S is enabled.
- Notes can be deleted by choosing to edit them and then clicking the Delete button. User will be prompted with a Confirmation dialog to complete this action.


----
Screenshots are below
----

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> Index page shows paginated list of notes:</span>

![Alt text](index.png?raw=true "Index page, listing notes - paginated")

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> Adding a new note:</span>

![Alt text](new_note.png?raw=true "Adding new note")

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> Viewing a note:</span>

![Alt text](viewing-note.png?raw=true "Viewing a note")

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> Editing a note:</span>

![Alt text](edit-note.png?raw=true "Editing a note")

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> Search notes form:</span>

![Alt text](search1.png?raw=true "Search notes")

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> Search results shown below, paginated:</span>

![Alt text](search-results.png?raw=true "Search results - paginated")

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> Export multiple notes to PDF:</span>

![Alt text](export-to-pdf.png?raw=true "Export multiple notes to PDF")

<span style="color:IndianRed; font-size: 150%; font-weight: bold;"> This is what an exported note looks like in PDF viewer:</span>

![Alt text](exported-note.png?raw=true "Exported note in PDF viewer")


