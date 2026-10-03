# Shile — Minecraft Fabric 26.2 Korku Modu

> *"Arkana bakma... O zaten seni izliyor."*

**Shile**, Minecraft oyuncusunun arkasında sinsice ortaya çıkan, dairesel bir yörünge izleyerek oyuncunun önüne geçmeye çalışan ve bakış açısıyla tetiklenen gerilim odaklı bir istemci-sunucu (client-server) korku modudur.

---

## Konsept ve İlham Kaynağı
Shile, temel ilhamını Roblox DOORS içerisindeki **Alma** varlığından almaktadır. Ancak kendine has dairesel hareket matematiği, bakış odaklı Quick-Time Event (QTE) mini oyunu ve sunucu otoritesine sahip ani ölüm mekaniğiyle özgün bir Minecraft deneyimi sunar.

---

## Temel Mekanikler

1. **Sinsi Ortaya Çıkış:**
   * Shile, oyuncunun tam arkasında yaklaşık 2 blok mesafede belirir.
   * Oyuncuya doğrudan yaklaşmaz veya aniden önüne ışınlanmaz.

2. **Dairesel Yörünge (6 saniye / 360°):**
   * Oyuncunun etrafında dairesel bir yay izleyerek en kısa yoldan (saat yönü veya tersi) oyuncunun önüne geçmeye çalışır.
   * Açısal hız: $60^\circ/\text{saniye}$ ($\frac{\pi}{3}\text{ rad/s}$).

3. **Bakış Algılama (POV Raycast):**
   * Oyuncu Shile'a aralıksız $1.0\text{ saniye}$ ($20\text{ tick}$) boyunca doğrudan bakarsa zorunlu mini oyun tetiklenir.
   * Bloklar görüşü engellerse bakış sayacı sıfırlanır.

4. **QTE Mini Oyunu:**
   * Ekranda beliren tüm açık Minecraft arayüzleri kapatılır.
   * Rastgele konumlarda klavye harfleri ($A-Z$) belirir.
   * Oyuncunun her tuş için yalnızca $1.0\text{ saniyesi}$ vardır.
   * Oyuncu $15.0\text{ saniye}$ boyunca zamanında doğru tuşlara basarak hayatta kalmalıdır.

5. **Sunucu Otoritesi ve Ani Ölüm:**
   * Yanlış tuşa basılması veya süre aşımı durumunda sunucu oyuncuyu anında cezalandırır (ani ölüm).
   * 15 saniye dayanıldığında Shile kaybolur ve 8–25 dakika arası rastgele bekleme süresi (cooldown) başlar.

---

## Teknik Özellikler ve Mimari

* **Platform:** Fabric 26.2
* **Java:** 21+
* **Ağ Protokolü:** Modern Fabric `CustomPacketPayload` (Type-safe records & StreamCodec)
* **Görselleştirme:** Hafif istemci projeksiyonu (`WorldRenderEvents` billboard quad) — Çok oyunculu sunucularda sıfır mob yükü ve diğer oyuncuların göremeyeceği tam gizlilik.

---

## Geliştirici & Lisans
* **Geliştirici:** seezzalith
* **Proje Durumu:** Aktif Geliştirme (Faz 1)
