import pyupbit
import pymysql

con = pymysql.connect(host='diary.cu6lgsxzmymo.ap-northeast-2.rds.amazonaws.com',
                      port=3306,
                      user='diary',
                      password='rnfma^123',
                      db='crypto',
                      charset='utf8')

cur = con.cursor()

query = "delete from price_bitCoin;"
cur.execute(query)
query = "delete from price_etherium;"
cur.execute(query)

con.commit()

df = pyupbit.get_ohlcv("KRW-BTC", count=2400, interval="minute60")
query = "insert into price_bitCoin (open, high, low, close, volume, time) values (%s, %s, %s, %s, %s, %s);"
lists = []
for index, row in df.iterrows():
    tuple = (row['open'], row['high'], row['low'], row['close'], row['volume'], str(index))
    lists.append(tuple)
cur.executemany(query, lists)

df = pyupbit.get_ohlcv("KRW-ETH", count=2400, interval="minute60")
query = "insert into price_etherium (open, high, low, close, volume, time) values (%s, %s, %s, %s, %s, %s);"
lists = []
for index, row in df.iterrows():
    tuple = (row['open'], row['high'], row['low'], row['close'], row['volume'], str(index))
    lists.append(tuple)

cur.executemany(query, lists)
con.commit();
con.close()