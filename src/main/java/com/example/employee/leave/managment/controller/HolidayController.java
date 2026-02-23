package com.example.employee.leave.managment.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/api")
public class HolidayController {

    @GetMapping("/holidays")
    public ResponseEntity<List<Map<String, Object>>> getHolidays(
            @RequestParam(value = "year", required = false) Integer year) {

        if (year == null) year = LocalDate.now().getYear();
        return ResponseEntity.ok(getHolidaysForYear(year));
    }

    private List<Map<String, Object>> getHolidaysForYear(int year) {
        // Each entry: { "date", "name" (English), "localName" (Hindi), "type" }
        List<Object[]> data = new ArrayList<>();

        switch (year) {

            case 2025:
                data.add(h("2025-01-14", "Makar Sankranti / Pongal", "मकर संक्रांति / पोंगल", "National"));
                data.add(h("2025-01-26", "Republic Day", "गणतंत्र दिवस", "National"));
                data.add(h("2025-02-26", "Maha Shivaratri", "महाशिवरात्रि", "National"));
                data.add(h("2025-03-14", "Holi", "होली", "National"));
                data.add(h("2025-03-31", "Id-ul-Fitr (Eid)", "ईद-उल-फित्र", "National"));
                data.add(h("2025-04-10", "Mahavir Jayanti", "महावीर जयंती", "National"));
                data.add(h("2025-04-14", "Dr. Ambedkar Jayanti", "डॉ. अम्बेडकर जयंती", "National"));
                data.add(h("2025-04-18", "Good Friday", "गुड फ्राइडे", "National"));
                data.add(h("2025-04-30", "Ram Navami", "राम नवमी", "National"));
                data.add(h("2025-05-12", "Buddha Purnima", "बुद्ध पूर्णिमा", "National"));
                data.add(h("2025-06-07", "Id-ul-Zuha (Bakrid)", "बकरीद", "National"));
                data.add(h("2025-07-06", "Muharram", "मुहर्रम", "National"));
                data.add(h("2025-08-15", "Independence Day", "स्वतंत्रता दिवस", "National"));
                data.add(h("2025-08-16", "Janmashtami", "जन्माष्टमी", "National"));
                data.add(h("2025-09-05", "Milad-un-Nabi", "ईद-ए-मिलाद", "National"));
                data.add(h("2025-10-02", "Gandhi Jayanti", "गांधी जयंती", "National"));
                data.add(h("2025-10-02", "Navratri Begins", "नवरात्री प्रारम्भ", "National"));
                data.add(h("2025-10-20", "Diwali / Lakshmi Puja", "दीपावली / लक्ष्मी पूजा", "National"));
                data.add(h("2025-10-21", "Govardhan Puja", "गोवर्धन पूजा", "National"));
                data.add(h("2025-11-05", "Guru Nanak Jayanti", "गुरु नानक जयंती", "National"));
                data.add(h("2025-12-25", "Christmas Day", "क्रिसमस", "National"));
                break;

            case 2026:
                data.add(h("2026-01-14", "Makar Sankranti / Pongal", "मकर संक्रांति / पोंगल", "National"));
                data.add(h("2026-01-26", "Republic Day", "गणतंत्र दिवस", "National"));
                data.add(h("2026-02-26", "Maha Shivaratri", "महाशिवरात्रि", "National"));
                data.add(h("2026-03-03", "Holi", "होली", "National"));
                data.add(h("2026-03-30", "Ram Navami", "राम नवमी", "National"));
                data.add(h("2026-04-02", "Mahavir Jayanti", "महावीर जयंती", "National"));
                data.add(h("2026-04-03", "Good Friday", "गुड फ्राइडे", "National"));
                data.add(h("2026-04-14", "Dr. Ambedkar Jayanti", "डॉ. अम्बेडकर जयंती", "National"));
                data.add(h("2026-04-14", "Vaisakhi / Vishu", "वैसाखी / विशु", "National"));
                data.add(h("2026-05-13", "Buddha Purnima", "बुद्ध पूर्णिमा", "National"));
                data.add(h("2026-06-20", "Id-ul-Fitr (Eid)", "ईद-उल-फित्र", "National"));
                data.add(h("2026-08-15", "Independence Day", "स्वतंत्रता दिवस", "National"));
                data.add(h("2026-08-22", "Raksha Bandhan", "रक्षाबंधन", "National"));
                data.add(h("2026-09-02", "Janmashtami", "जन्माष्टमी", "National"));
                data.add(h("2026-09-18", "Milad-un-Nabi", "ईद-ए-मिलाद", "National"));
                data.add(h("2026-10-02", "Gandhi Jayanti", "गांधी जयंती", "National"));
                data.add(h("2026-10-20", "Dussehra (Vijayadashami)", "दशहरा (विजयादशमी)", "National"));
                data.add(h("2026-11-01", "Diwali / Lakshmi Puja", "दीपावली / लक्ष्मी पूजा", "National"));
                data.add(h("2026-11-02", "Govardhan Puja / Bhai Dooj", "गोवर्धन पूजा / भाई दूज", "National"));
                data.add(h("2026-11-15", "Guru Nanak Jayanti", "गुरु नानक जयंती", "National"));
                data.add(h("2026-12-25", "Christmas Day", "क्रिसमस", "National"));
                break;

            case 2027:
                data.add(h("2027-01-14", "Makar Sankranti", "मकर संक्रांति", "National"));
                data.add(h("2027-01-26", "Republic Day", "गणतंत्र दिवस", "National"));
                data.add(h("2027-02-15", "Maha Shivaratri", "महाशिवरात्रि", "National"));
                data.add(h("2027-03-22", "Holi", "होली", "National"));
                data.add(h("2027-04-14", "Dr. Ambedkar Jayanti", "डॉ. अम्बेडकर जयंती", "National"));
                data.add(h("2027-04-23", "Good Friday", "गुड फ्राइडे", "National"));
                data.add(h("2027-05-01", "May Day / Labour Day", "मजदूर दिवस", "National"));
                data.add(h("2027-05-31", "Buddha Purnima", "बुद्ध पूर्णिमा", "National"));
                data.add(h("2027-08-15", "Independence Day", "स्वतंत्रता दिवस", "National"));
                data.add(h("2027-08-18", "Janmashtami", "जन्माष्टमी", "National"));
                data.add(h("2027-10-02", "Gandhi Jayanti", "गांधी जयंती", "National"));
                data.add(h("2027-10-17", "Dussehra", "दशहरा", "National"));
                data.add(h("2027-10-28", "Diwali / Lakshmi Puja", "दीपावली / लक्ष्मी पूजा", "National"));
                data.add(h("2027-11-15", "Guru Nanak Jayanti", "गुरु नानक जयंती", "National"));
                data.add(h("2027-12-25", "Christmas Day", "क्रिसमस", "National"));
                break;

            default:
                // For other years, return current year's data as best-effort
                if (year != LocalDate.now().getYear()) {
                    return getHolidaysForYear(LocalDate.now().getYear());
                }
                break;
        }

        // Sort by date
        data.sort(Comparator.comparing(o -> (String) o[0]));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] entry : data) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date",      entry[0]);
            m.put("name",      entry[1]);
            m.put("localName", entry[2]);
            m.put("type",      entry[3]);
            result.add(m);
        }
        return result;
    }

    private Object[] h(String date, String name, String localName, String type) {
        return new Object[]{date, name, localName, type};
    }
}