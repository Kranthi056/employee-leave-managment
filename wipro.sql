
geological:
create table olist_geolocation(geolocation int not null, geolocation_lat Decimal,geolocation_lng Decimal,geolocation_city varchar(20),geolocation_state varchar(20));


order items:
create table orderitems(order_id varchar(20)not null ,order_item_id int,product_id varchar(20), seller_id varchar(20),shipping_limit_date date, price int, freight_value int,payment_sequentical int,payment_type varchar(20),payment_installment int,payment_value Decimal);

payments:
create table order_payments(order_id varchar(20) not null,payment_sequential int, payment_type varchar(20),payment_installment int , payment_value decimal); 

orders:
create table olist_orders(order_id varchar(20) not null,customer_id varchar(20),order_status varchar(20),order_purchase date,order_approved date,order_delivered_carrier_date date,order_delivered_customer_date date,order_estimated_date date,delivery_date date);


seller:
create table olist_sellers(seller_id varchar(20) not null,seller_zip_code int,seller_city varchar(20),seller_state varchar(20));

products 
create table olist_products(product_id varchar(20), product_category_name varchar(20),product_name_length int, product_description int , product_photos_qty int, product_weight_g int, product_length_cm int, product_height_cm int,product_width_cm int);


-- olist_order_reviews.
create order_reviews(review_id varchar(20) , order_id varchar(20), review_score int, review_comment_title varchar(20),review_comment_moral varchar(20),review_creation_date date,review_answer_timestamp date);