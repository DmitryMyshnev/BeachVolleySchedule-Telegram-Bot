CREATE TABLE "Users" (
	"id"	INTEGER NOT NULL,
	"first_name"	TEXT NOT NULL,
	"last_name"	TEXT,
	"user_id"	TEXT NOT NULL UNIQUE,
	"admin"	INTEGER DEFAULT 0,
	"coach"	INTEGER DEFAULT 0,
	PRIMARY KEY("id" AUTOINCREMENT)
);
SELECT chat_id, first_name,last_name,admin,coach FROM Users WHERE chat_id = 6523595;