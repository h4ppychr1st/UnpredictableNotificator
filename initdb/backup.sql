--
-- PostgreSQL database dump
--

\restrict iDFRlfVOaespZDYc5g6m5wC1dirw7KvseuByrk4D7TvUHmhYZWBeZLWTwDYrcwW

-- Dumped from database version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: coffee_orders; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.coffee_orders (
    id bigint NOT NULL,
    user_id integer NOT NULL,
    ts timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.coffee_orders OWNER TO serj;

--
-- Name: coffee_orders_id_seq; Type: SEQUENCE; Schema: public; Owner: serj
--

CREATE SEQUENCE public.coffee_orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.coffee_orders_id_seq OWNER TO serj;

--
-- Name: coffee_orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: serj
--

ALTER SEQUENCE public.coffee_orders_id_seq OWNED BY public.coffee_orders.id;


--
-- Name: lessons; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.lessons (
    id bigint NOT NULL,
    user_id integer NOT NULL,
    started_at timestamp with time zone NOT NULL,
    ended_at timestamp with time zone
);


ALTER TABLE public.lessons OWNER TO serj;

--
-- Name: lessons_id_seq; Type: SEQUENCE; Schema: public; Owner: serj
--

CREATE SEQUENCE public.lessons_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lessons_id_seq OWNER TO serj;

--
-- Name: lessons_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: serj
--

ALTER SEQUENCE public.lessons_id_seq OWNED BY public.lessons.id;


--
-- Name: messages; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.messages (
    id bigint NOT NULL,
    user_id integer NOT NULL,
    ts timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.messages OWNER TO serj;

--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: serj
--

CREATE SEQUENCE public.messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.messages_id_seq OWNER TO serj;

--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: serj
--

ALTER SEQUENCE public.messages_id_seq OWNED BY public.messages.id;


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.notifications (
    id bigint NOT NULL,
    user_id integer NOT NULL,
    message text NOT NULL,
    ts timestamp with time zone DEFAULT now()
);


ALTER TABLE public.notifications OWNER TO serj;

--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: serj
--

CREATE SEQUENCE public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.notifications_id_seq OWNER TO serj;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: serj
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- Name: owls; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.owls (
    user_id integer NOT NULL,
    candidate_for date NOT NULL,
    notified_at timestamp with time zone
);


ALTER TABLE public.owls OWNER TO serj;

--
-- Name: reminders; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.reminders (
    id bigint NOT NULL,
    user_id integer NOT NULL,
    remind_at timestamp with time zone NOT NULL
);


ALTER TABLE public.reminders OWNER TO serj;

--
-- Name: reminders_id_seq; Type: SEQUENCE; Schema: public; Owner: serj
--

CREATE SEQUENCE public.reminders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reminders_id_seq OWNER TO serj;

--
-- Name: reminders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: serj
--

ALTER SEQUENCE public.reminders_id_seq OWNED BY public.reminders.id;


--
-- Name: rewards; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.rewards (
    user_id integer NOT NULL,
    reward_date date NOT NULL,
    coupon_used boolean DEFAULT true NOT NULL
);


ALTER TABLE public.rewards OWNER TO serj;

--
-- Name: tickets; Type: TABLE; Schema: public; Owner: serj
--

CREATE TABLE public.tickets (
    id bigint NOT NULL,
    user_id integer NOT NULL,
    ts timestamp with time zone DEFAULT now(),
    notified boolean DEFAULT false NOT NULL
);


ALTER TABLE public.tickets OWNER TO serj;

--
-- Name: tickets_id_seq; Type: SEQUENCE; Schema: public; Owner: serj
--

CREATE SEQUENCE public.tickets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tickets_id_seq OWNER TO serj;

--
-- Name: tickets_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: serj
--

ALTER SEQUENCE public.tickets_id_seq OWNED BY public.tickets.id;


--
-- Name: coffee_orders id; Type: DEFAULT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.coffee_orders ALTER COLUMN id SET DEFAULT nextval('public.coffee_orders_id_seq'::regclass);


--
-- Name: lessons id; Type: DEFAULT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.lessons ALTER COLUMN id SET DEFAULT nextval('public.lessons_id_seq'::regclass);


--
-- Name: messages id; Type: DEFAULT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.messages ALTER COLUMN id SET DEFAULT nextval('public.messages_id_seq'::regclass);


--
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- Name: reminders id; Type: DEFAULT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.reminders ALTER COLUMN id SET DEFAULT nextval('public.reminders_id_seq'::regclass);


--
-- Name: tickets id; Type: DEFAULT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.tickets ALTER COLUMN id SET DEFAULT nextval('public.tickets_id_seq'::regclass);


--
-- Data for Name: coffee_orders; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.coffee_orders (id, user_id, ts) FROM stdin;
28	3	2025-11-17 20:10:05+03
29	3	2025-11-19 20:10:05+03
30	3	2025-11-21 20:10:05+03
\.


--
-- Data for Name: lessons; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.lessons (id, user_id, started_at, ended_at) FROM stdin;
1	4	2025-11-21 20:10:05+03	2025-11-21 21:10:05+03
2	4	2025-11-22 21:10:05+03	2025-11-22 22:35:05+03
3	4	2025-11-23 22:35:05+03	2025-11-24 00:25:05+03
4	4	2025-11-24 00:25:05+03	2025-11-24 00:55:05+03
5	4	2025-11-25 00:55:05+03	2025-11-25 01:55:05+03
6	4	2025-11-25 18:55:05+03	2025-11-25 20:25:05+03
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.messages (id, user_id, ts) FROM stdin;
52	1	2025-11-15 03:41:53+03
53	1	2025-11-15 22:41:53+03
54	1	2025-11-16 22:41:53+03
55	1	2025-11-17 09:41:53+03
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.notifications (id, user_id, message, ts) FROM stdin;
3	1	Кажется, вы сова 🦉	2025-11-17 09:41:53+03
4	3	Заметили, вы часто покупаете кофе по пятницам, средам и понедельникам, держите купон на скидку во вторник!	2025-11-21 20:10:05+03
5	1	Очень надеюсь, что вы помните для чего вы оставили это напоминание 😐	2025-11-17 20:15:23.818446+03
6	2	Очень надеюсь, что вы помните для чего вы оставили это напоминание 😐	2025-11-17 20:20:23.868814+03
8	4	Ваше занятие длилось заметно дольше предыдущего! Так держать! 💪	2025-11-25 20:25:05+03
9	5	Вы проехали уйму поездок за последний месяц 🚌, следующая поездка будет бесплатной	2025-11-25 21:05:05+03
\.


--
-- Data for Name: owls; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.owls (user_id, candidate_for, notified_at) FROM stdin;
1	2025-11-16	2025-11-17 20:09:16.540372+03
\.


--
-- Data for Name: reminders; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.reminders (id, user_id, remind_at) FROM stdin;
\.


--
-- Data for Name: rewards; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.rewards (user_id, reward_date, coupon_used) FROM stdin;
3	2025-11-21	f
\.


--
-- Data for Name: tickets; Type: TABLE DATA; Schema: public; Owner: serj
--

COPY public.tickets (id, user_id, ts, notified) FROM stdin;
27	5	1975-08-04 21:32:39+03	f
28	5	1975-08-04 21:32:39+03	f
23	5	2025-11-25 20:25:05+03	t
24	5	2025-11-25 20:25:06+03	t
25	5	2025-11-25 20:25:07+03	t
26	5	2025-11-25 20:25:08+03	t
29	5	2025-11-25 20:26:45+03	t
30	5	2025-11-25 20:28:25+03	t
31	5	2025-11-25 20:30:05+03	t
32	5	2025-11-25 20:31:45+03	t
33	5	2025-11-25 20:48:25+03	t
34	5	2025-11-25 21:05:05+03	t
\.


--
-- Name: coffee_orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: serj
--

SELECT pg_catalog.setval('public.coffee_orders_id_seq', 30, true);


--
-- Name: lessons_id_seq; Type: SEQUENCE SET; Schema: public; Owner: serj
--

SELECT pg_catalog.setval('public.lessons_id_seq', 6, true);


--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: serj
--

SELECT pg_catalog.setval('public.messages_id_seq', 55, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: serj
--

SELECT pg_catalog.setval('public.notifications_id_seq', 9, true);


--
-- Name: reminders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: serj
--

SELECT pg_catalog.setval('public.reminders_id_seq', 10, true);


--
-- Name: tickets_id_seq; Type: SEQUENCE SET; Schema: public; Owner: serj
--

SELECT pg_catalog.setval('public.tickets_id_seq', 34, true);


--
-- Name: coffee_orders coffee_orders_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.coffee_orders
    ADD CONSTRAINT coffee_orders_pkey PRIMARY KEY (id);


--
-- Name: lessons lessons_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_pkey PRIMARY KEY (id);


--
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- Name: rewards mon_wed_fri_rewards_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.rewards
    ADD CONSTRAINT mon_wed_fri_rewards_pkey PRIMARY KEY (user_id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: owls owls_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.owls
    ADD CONSTRAINT owls_pkey PRIMARY KEY (user_id);


--
-- Name: reminders reminders_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.reminders
    ADD CONSTRAINT reminders_pkey PRIMARY KEY (id);


--
-- Name: tickets tickets_pkey; Type: CONSTRAINT; Schema: public; Owner: serj
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_pkey PRIMARY KEY (id);


--
-- Name: idx_coffee_orders_ts; Type: INDEX; Schema: public; Owner: serj
--

CREATE INDEX idx_coffee_orders_ts ON public.coffee_orders USING btree (ts);


--
-- Name: idx_coffee_orders_user_ts; Type: INDEX; Schema: public; Owner: serj
--

CREATE INDEX idx_coffee_orders_user_ts ON public.coffee_orders USING btree (user_id, ts);


--
-- Name: idx_lessons_user_start; Type: INDEX; Schema: public; Owner: serj
--

CREATE INDEX idx_lessons_user_start ON public.lessons USING btree (user_id, started_at);


--
-- Name: idx_messages_ts; Type: INDEX; Schema: public; Owner: serj
--

CREATE INDEX idx_messages_ts ON public.messages USING btree (ts);


--
-- Name: idx_messages_user_ts; Type: INDEX; Schema: public; Owner: serj
--

CREATE INDEX idx_messages_user_ts ON public.messages USING btree (user_id, ts);


--
-- Name: idx_owls_for; Type: INDEX; Schema: public; Owner: serj
--

CREATE INDEX idx_owls_for ON public.owls USING btree (candidate_for) WHERE (notified_at IS NULL);


--
-- Name: idx_tickets_user_created_notified; Type: INDEX; Schema: public; Owner: serj
--

CREATE INDEX idx_tickets_user_created_notified ON public.tickets USING btree (user_id, ts DESC) WHERE (notified = false);


--
-- PostgreSQL database dump complete
--

\unrestrict iDFRlfVOaespZDYc5g6m5wC1dirw7KvseuByrk4D7TvUHmhYZWBeZLWTwDYrcwW

