select database();
use new_schema;
create table customer(customer_id varchar(20),customer_unqiue_id varchar(20),customer_zip_code int , customer_city varchar(20),customer_state varchar(20));
select * from new_schema.customer;

create table geolocation(geolocation int,geolocation_lat decimal(9,6) ,geo_location_lng decimal(9,6),geolocation_city varchar(20),geolocation_state varchar(20));
select * from new_schema.geolocation;

create table orderitems(order_id varchar(20) not null ,order_item_id varchar(20),product_id varchar(20),seller_id varchar(20),shipping_limit_date date,price int,freight_value int,payment_sequential int , payment_type varchar(20),payment_installment int, payment_value decimal);
select * from new_schema.orderitems;

create table order_payment(order_id varchar(20) not null,payment_sequentical int , payment_type varchar(20),payment_installments int, payment_value decimal);
select * from new_schema.order_payment;

create table orders(order_id varchar(20),customer_id varchar(20) primary key ,order_status varchar(20),order_purchase date ,order_approved date,order_delivered_carrier_date date,order_delivered_customer_date date,order__estimated_date date , delivery_date date);
select * from new_schema.orders;

create table sellers(sellers_id varchar(20),seller_zip_code int , seller_city varchar(20),seller_state varchar(20));
select * from new_schema.sellers;

create table products(product_id varchar(20) not null,product_category_name varchar(20),product_name_length_cm int,product_photos_qty int,product_weight_g int,product_lenth_cm int, product_height_cm int,product_width_cm int);
select * from new_schema.prodcuts;

create table reviews(review_id varchar(20) not null,order_id varchar(20),review_score int ,review_comment_title varchar(20),review_commnet_moral varchar(20),review_creation_date date , review_answer date);
select * from new_schema.reviews;

show tables;


