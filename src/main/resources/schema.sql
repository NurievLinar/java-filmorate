create table IF NOT EXISTS USERS
(
    USER_ID       INTEGER auto_increment,
    USER_EMAIL    CHARACTER VARYING(50) not null,
    USER_LOGIN    CHARACTER VARYING(50) not null,
    USER_NAME     CHARACTER VARYING(50),
    USER_BIRTHDAY DATE,
    constraint USERS_PK
        primary key (USER_ID)
);
create table IF NOT EXISTS MPA
(
    MPA_ID   INTEGER generated by default as identity (exhausted),
    MPA_NAME VARCHAR(10) not null,
    constraint MPA_PK
        primary key (MPA_ID)
);
create table IF NOT EXISTS FILMS
(
    FILM_ID             INTEGER auto_increment,
    FILM_NAME           CHARACTER VARYING(50)  not null,
    MPA_ID              INTEGER                not null,
    FILM_DESCRIPTION    CHARACTER VARYING(200) not null,
    FILM_RELEASE_DATE   DATE                   not null,
    FILM_DURATION       INTEGER                not null,
    FILM_RATE           INTEGER default 0,
    FILM_RATE_AND_LIKES INTEGER default 0,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_FK
        foreign key (MPA_ID) references MPA
);
create table IF NOT EXISTS GENRE
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(20) not null,
    constraint GENRE_PK
        primary key (GENRE_ID)
);
create table IF NOT EXISTS FRIENDSHIP_REQUESTS
(
    SENDER_ID    INTEGER               not null,
    RECIPIENT_ID INTEGER               not null,
    PROOF        BOOLEAN default FALSE not null,
    constraint FRIENDSHIP_REQUESTS_SENDER_USER_ID_FK
        foreign key (SENDER_ID) references USERS (USER_ID),
    constraint FRIENDSHIP_REQUESTS_RECIPIENT_USER_ID_FK
        foreign key (RECIPIENT_ID) references USERS (USER_ID)
);
create table IF NOT EXISTS FILM_TO_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_TO_GENRE_FILMS_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILM_TO_GENRE_GENRE_FK
        foreign key (GENRE_ID) references GENRE
);
create table IF NOT EXISTS USER_LIKE_FILM
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint USER_LIKE_FILM_USERS_ID_FK
        foreign key (USER_ID) references USERS (USER_ID),
    constraint USER_LIKE_FILM_FILMS_ID_FK
        foreign key (FILM_ID) references FILMS (FILM_ID)
);