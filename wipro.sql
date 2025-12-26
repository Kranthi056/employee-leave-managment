show databases;
use new_schema;
create table products(
product_id varchar(50) primary key,
product_category_name varchar(100),
product_category_number int,
product_name_lenght int,
product_description_length int,
product_photos_qty int,
product_weight_g int,
product_length_cm int,
product_height_cm int,
product_width_cm int
);
desc products;
select * from products;
show tables;

create table sellers(
seller_id varchar(50) primary key,
seller_zip_code_prefix int,
seller_city varchar(100),
seller_state varchar(10)
);
create table customers(
customer_id varchar(50) primary key,
customer_unique_id varchar(50),
customer_zip_code_prefix int,
customer_city varchar(100),
customer_state varchar(20)
);

create table orders(
order_id varchar(50) primary key,
customer_id varchar(50) references customers(customer_id),
order_status varchar(40),
order_purchase_timestamp timestamp,
order_approved_at timestamp,
order_delivered_carrier_date timestamp,
order_delivered_customer_date timestamp,
order_estimated_delivery_date timestamp
);

create table order_items(
order_id varchar(50) references orders(order_id),
order_item_id int,
product_id varchar(50) references products(product_id),
seller_id varchar(50)  references seller(seller_id),
shipping_limit_date timestamp,
price decimal(10,2),
freight_value decimal(10,2));

create table geolocation(
geolocation_zip_code_prefix int,
geolocation_lat decimal(20,19),
geolocation_lng decimal(20,19),
geolocation_city varchar(50),
geolocation_state varchar(50)
);

create table order_payments(
order_id varchar(50) references orders(order_id),
payment_sequential int,
payment_type varchar(30),
payment_installmemts int,
payment_value decimal(15,2)
);

create table order_reviews(
review_id int primary key,
order_id varchar(50) references orders(order_id),
review_score int,
review_comment_title varchar(200),
review_comment_message varchar(200),
review_creation_date timestamp,
review_answer_timestamp timestamp
);

