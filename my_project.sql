show databases;
use wipro;
show tables;
select * from wipro.customers_dataset;

#customer_datasets
-- 1. List all customers from the state SP.
select * from wipro.customers_dataset where customer_state='SP';

-- 2. Find customers whose city starts with Rio.
select *
from wipro.customers_dataset
where customer_city like 'Rio%';

-- 3 Count customers per state.
select customer_state,count(*) as customerstate from wipro.customers_dataset group by customer_state;

-- 4. Display customers with ZIP code prefix between `10000` and `20000`.
select* from wipro.customers_dataset where customer_zip_code_prefix between 10000 and 20000;

-- 5. Show distinct cities where customers live.
select distinct(customer_city) from wipro.customers_dataset;

-- Geolocation data set

-- 6 Find geolocations in SP state.
select * from wipro.olist_geolocation_dataset where geolocation_state = 'SP';

-- 7 Count number of geolocations per city.
select geolocation_city,count(*) as geolocationcity from wipro.olist_geolocation_dataset group by geolocation_city;

-- 8 Find average latitude and longitude per state.
select * from wipro.olist_geolocation_dataset;
select geolocation_state ,
avg(geolocation_lat) ,
avg(geolocation_lng)
from wipro.olist_geolocation_dataset group by geolocation_state;

-- 9 Show geolocations where city name contains Santos.
select * from wipro.olist_geolocation_dataset where geolocation_city like '%Santo%';

-- 10 Find top 5 states with most geolocations.
select geolocation_state,count(*) as geolocationstate
from wipro.olist_geolocation_dataset
group by geolocation_state
order by geolocationstate desc
limit 5;

-- 11.List all orders with status delivered.
select * from wipro.olist_orders_dataset where order_status='delivered';

-- 12 Find orders placed in 2017.
select * from wipro.olist_orders_dataset where (order_purchase_timestamp) =2017;

-- 13 count orders per status.
select order_status , count(*) as orderstatus from wipro.olist_orders_dataset group by order_status;

-- 14 Find average delivery time (customer date - purchase date).
select avg(datediff(order_delivered_customer_date, order_purchase_timestamp))
as avg_delivery_days
from wipro.olist_orders_dataset
where order_delivered_customer_date is not null;

-- 15. Show orders where estimated delivery date < actual delivery date.
select order_id, customer_id,
order_estimated_delivery_date,
order_delivered_customer_date
from wipro.olist_orders_dataset
where order_delivered_customer_date > order_estimated_delivery_date;

-- Order Items
-- 16. Find total sales value per order.
select * from wipro.olist_order_items_dataset;

select order_id , sum(price) as totalprice
from wipro.olist_order_items_dataset
group by order_id;
-- 17. Find average freight value per seller.
select seller_id, avg(freight_value) as avgfreightvalue
from wipro.olist_order_items_dataset
group by seller_id;

-- 18. Show orders with more than 3 items.
select order_id, count(*) as num_items
from wipro.olist_order_items_dataset
group by order_id
having count(*) > 3;


-- 19. Find most expensive product sold.
select*  from wipro.olist_order_items_dataset
order by price desc
limit 1;

-- 20. List sellers who sold products with freight value> 100.
select seller_id , freight_value from wipro.olist_order_items_dataset where freight_value>100;







-- Order Payments
-- 21. Find total payment per order.
select order_id, sum(payment_value) as total_payment
from wipro.olist_order_payments_dataset
group by order_id;

-- 22. Count number of payments by type.
select * from wipro.olist_order_payments_dataset;
select payment_type , count(*) as paymenttype from wipro.olist_order_payments_dataset group by payment_type;

-- 23. Find average installments for credit card payments.
SELECT avg (payment_installments)
FROM wipro.olist_order_payments_dataset
WHERE payment_type = 'credit_card';

-- 24. Show orders with payment value > 1000.
select * from wipro.olist_order_payments_dataset where payment_value>1000;

-- 25. Find top 5 orders with highest payment value.
select order_id, sum(payment_value) as total_payment
from wipro.olist_order_payments_dataset
group by order_id
order by total_payment desc
limit 5;

## Products
-- 26. List products with more than 5 photos.
select * from wipro.olist_products_dataset;
select * from wipro.olist_products_dataset
where product_photos_qty>5;

-- 27.Find average weight of products per category.
select product_category_name , avg(product_weight_g) as totalproductweight
from wipro.olist_products_dataset
group by product_category_name;

-- 28.Show products where description length > 1000.
select product_category_name , product_description_lenght
from wipro.olist_products_dataset
where product_description_lenght>1000;

-- 29. Find categories with most products.
select product_category_name , count(*) as productname
from wipro.olist_products_dataset
group by product_category_name
order by product_category_name desc;

-- 30. Show products with length > width.
select *
from wipro.olist_products_dataset
where product_length_cm > product_width_cm;

## Order Reviews
select*from wipro.olist_order_reviews_dataset;
-- 31. Find average review score per order.
select order_id , avg(review_score) as avgreviewscore
from wipro.olist_order_reviews_dataset
group by order_id
order by avgreviewscore desc;

-- 32. Show reviews with score = 5.
select * from wipro.olist_order_reviews_dataset where review_score=5;


-- 33. Count reviews per score.
select review_score, count(*) as reviewscore
from wipro.olist_order_reviews_dataset
group by review_score
order by reviewscore;
-- 34. Find reviews created in January 2018.
select * from wipro.olist_order_reviews_dataset
where year(review_creation_date) = 2018
  and month(review_creation_date) = 1;


-- 35. Show reviews where comment contains late.
select * from wipro.olist_order_reviews_dataset
where lower(review_comment_message) like '%late%';


## Sellers
select * from wipro.olist_sellers_dataset;
-- 36. List sellers from SP.
select * from wipro.olist_sellers_dataset where seller_state='SP';

-- 37. Count sellers per city.
SELECT seller_city, COUNT(*) AS sellers_count
FROM wipro.olist_sellers_dataset
GROUP BY seller_city
ORDER BY sellers_count DESC;


-- 38. Find sellers with ZIP prefix starting with 01.
SELECT seller_id, seller_zip_code_prefix
FROM wipro.olist_sellers_dataset
WHERE seller_zip_code_prefix LIKE '01%';

-- 39. Show distinct states where sellers are located.
select distinct seller_state from wipro.olist_sellers_dataset;

-- 40. Find top 5 cities with most sellers.
SELECT seller_state, COUNT(*)
FROM wipro.olist_sellers_dataset
GROUP BY seller_state
ORDER BY COUNT(*) DESC
LIMIT 5;

-- 42. Find sellers where city name length > 10.
select seller_city from wipro.olist_sellers_dataset where length(seller_city)>10;

-- 43. Extract first 3 characters of customer state.
SELECT customer_state, LEFT(customer_state, 3) AS state_prefix
FROM wipro.customers_dataset;

-- 44. Replace SP with São Paulo in customer state.
SELECT customer_state, REPLACE(customer_state, 'SP', 'São Paulo') AS updated_state
FROM wipro.customers_dataset;



