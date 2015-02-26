DROP TABLE BOARD;

CREATE TABLE BOARD (
		BOARD_ID INTEGER GENERATED ALWAYS AS IDENTITY,
		BOARD_HEIGHT INTEGER,
		BOARD_WIDTH INTEGER,
		BOARD_DIFFICULTY_LEVEL INTEGER,
		BOARD_DIFFICULTY_TEXT VARCHAR(128),
		PRIMARY KEY (BOARD_ID)
	);

