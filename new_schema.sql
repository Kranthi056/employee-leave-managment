show database();
use new_schema;

-- 1.customer:
create table customer(customer_id varchar(20) Primary Key , customer_unique_id int,customer_zip_code int, customer_city varchar(20),customer_state varchar(20));
Select * from new_schema.customer;


-- 2.geological:
create table geolocation(geolocation int not null, geolocation_lat Decimal,geolocation_lng Decimal,geolocation_city varchar(20),geolocation_state varchar(20));
Select * from new_schema.geolocation;


-- 3.orderitems:
create table orderitems(order_id varchar(20) not null ,order_item_id int,product_id varchar(20), seller_id varchar(20),shipping_limit_date date, price int, freight_value int,payment_sequentical int,payment_type varchar(20),payment_installment int,payment_value Decimal);
Select * from new_schema.orderitems;

-- 4.orderpayments:
create table orderpayments(order_id varchar(20) not null,payment_sequential int, payment_type varchar(20),payment_installment int , payment_value decimal); 
Select * from new_schema.orderpayments;

-- 5.orders:
create table orders(order_id varchar(20) not null,customer_id varchar(20),order_status varchar(20),order_purchase date,order_approved date,order_delivered_carrier_date date,order_delivered_customer_date date,order_estimated_date date,delivery_date date);
Select * from new_schema.orders;


-- 6.seller:
create table seller(seller_id varchar(20) not null,seller_zip_code int,seller_city varchar(20),seller_state varchar(20));
Select * from new_schema.seller;

-- 7.products 
create table products(product_id varchar(20) not null, product_category_name varchar(20),product_name_length int, product_description int , product_photos_qty int, product_weight_g int, product_length_cm int, 
product_height_cm int,product_width_cm int);
Select * from new_schema.products;


-- 8.reviews
create table reviews(review_id varchar(20) not null , order_id varchar(20), review_score int, review_comment_title varchar(20),review_comment_moral varchar(20),review_creation_date date,review_answer_timestamp date);
Select * from new_schema.reviews;