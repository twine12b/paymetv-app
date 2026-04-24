
    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id integer not null,
        id bigint not null,
        front varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255),
        password varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    create table artifact (
        status bit,
        id bigint not null,
        image_faces_id bigint,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        front varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint UKs7i6rcia3ycph91wotsaoasx1 unique (image_faces_id);

    alter table artifact 
       add constraint FKbaw73oh674iikvfatuxrg7lwr 
       foreign key (image_faces_id) 
       references image_face (id);

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        front varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        front varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        front varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);

    create table artifact (
        status bit,
        id bigint not null,
        user_id bigint,
        description varchar(255),
        model varchar(255),
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table artifact_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into artifact_seq values ( 1 );

    create table image_face (
        artifact_id bigint not null,
        id bigint not null,
        `back` varchar(255) not null,
        `bottom` varchar(255),
        front varchar(255) not null,
        `left` varchar(255) not null,
        `right` varchar(255) not null,
        `top` varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table image_face_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into image_face_seq values ( 1 );

    create table users (
        id bigint not null,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into users_seq values ( 1 );

    alter table artifact 
       add constraint FK32av6ob3brqexul55soqfyo2c 
       foreign key (user_id) 
       references users (id);

    alter table image_face 
       add constraint FKcxhp5us8si04bem3kau73qtt0 
       foreign key (artifact_id) 
       references artifact (id);
