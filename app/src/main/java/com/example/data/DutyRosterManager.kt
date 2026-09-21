package com.example.data

data class CivilDuty(
    val id: String,
    val date: String,
    val month: String,
    val staffName: String,
    val activity: String,
    val stationCode: String,
    val stationName: String,
    val kmMarker: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val priority: String = "HIGH",
    val defaultRecurrence: String = "NONE" // NONE, DAILY, WEEKLY, MONTHLY
)

class DutyRosterManager {

    val allDuties: List<CivilDuty> = listOf(
        // MAY 2026
        CivilDuty("M01", "01.05.2026", "May 2026", "Suraj", "Chan yard unimate m/c work time 7:00", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Operations", "HIGH", "WEEKLY"),
        CivilDuty("M02", "01.05.2026", "May 2026", "Sudhir", "Chan yard unimate m/c work time 7:00", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Operations", "HIGH", "WEEKLY"),
        CivilDuty("M03", "01.05.2026", "May 2026", "Indrjeet", "Chan yard unimate m/c work time 7:00", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Operations", "HIGH"),
        CivilDuty("M04", "01.05.2026", "May 2026", "Rahul", "NSIR curve measurement curve no. 351", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Curve Measurement", "HIGH", "MONTHLY"),
        CivilDuty("M05", "01.05.2026", "May 2026", "Surjeet", "NSIR curve measurement curve no. 351", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Curve Measurement", "HIGH"),
        CivilDuty("M06", "01.05.2026", "May 2026", "Ranjeet", "Track inspection km 1174/5", "SMUN", "Shambhu New", "Km 1174.500", 30.3472, 76.7125, "Inspection", "MEDIUM", "WEEKLY"),
        CivilDuty("M07", "02.05.2026", "May 2026", "Rahul", "Curve measurement 352 km 1205/709", "NSIR", "New Sirhind", "Km 1205.709", 30.6354, 76.3881, "Curve Measurement", "HIGH"),
        CivilDuty("M08", "02.05.2026", "May 2026", "Karm singh", "Welding work km 1174/5", "RPJ", "Rajpura Link", "Km 1174.500", 30.4842, 76.5930, "Welding", "HIGH"),
        CivilDuty("M09", "03.05.2026", "May 2026", "Ranjeet", "Patrolling at 1170 - 1177 km", "SMUN", "Shambhu New", "Km 1170.000", 30.3472, 76.7125, "Inspection", "HIGH", "DAILY"),
        CivilDuty("M10", "04.05.2026", "May 2026", "Sudhir", "KNNN unimate machine work time 7:00", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Operations", "HIGH"),
        CivilDuty("M11", "04.05.2026", "May 2026", "Suraj", "KNNN unimate machine work time 7:00", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Operations", "HIGH"),
        CivilDuty("M12", "04.05.2026", "May 2026", "Rahul", "Point measurement at NSIR yard main line", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Point Work", "HIGH", "MONTHLY"),
        CivilDuty("M13", "05.05.2026", "May 2026", "Amrish", "Welding work at SBJN yard 1187/13", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Welding", "HIGH"),
        CivilDuty("M14", "05.05.2026", "May 2026", "Sunny", "Welding work at SBJN yard 1187/13", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Welding", "HIGH"),
        CivilDuty("M15", "07.05.2026", "May 2026", "Suraj", "Patrolling at LC 164 SEJ measurement oil & greasing", "LC 164", "Level Crossing 164", "Km 1233.500", 30.7300, 76.1600, "Maintenance", "HIGH", "WEEKLY"),
        CivilDuty("M16", "08.05.2026", "May 2026", "Sudhir", "Point measurement at KNNN yard", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Point Work", "HIGH"),
        CivilDuty("M17", "08.05.2026", "May 2026", "Gautam", "Point measurement at KNNN yard", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Point Work", "HIGH"),
        CivilDuty("M18", "09.05.2026", "May 2026", "Gautam", "Unimate machine at SBJN yard 7:00 am", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Operations", "HIGH"),
        CivilDuty("M19", "11.05.2026", "May 2026", "Sunny", "USFD team at km 1175 joggled fish plate opening/closing", "RPJ", "Rajpura Link", "Km 1175.000", 30.4842, 76.5930, "Inspection", "HIGH"),
        CivilDuty("M20", "12.05.2026", "May 2026", "Suraj", "JPC joint point check at KNNN", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Point Work", "HIGH", "MONTHLY"),
        CivilDuty("M21", "15.05.2026", "May 2026", "Gautam", "Office post tamping data analysis", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Inspection", "MEDIUM"),
        CivilDuty("M22", "20.05.2026", "May 2026", "Ranjeet", "SEJ gap measurement & oiling/greasing Br 108 & 115", "RPJ", "Bridge 108 / 115", "Km 1174.500", 30.4842, 76.5930, "Maintenance", "HIGH", "MONTHLY"),
        CivilDuty("M23", "21.05.2026", "May 2026", "Gautam", "Motor trolley inspection at CHAN", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Inspection", "HIGH"),
        CivilDuty("M24", "25.05.2026", "May 2026", "Amrish", "NSIR loop line points measurement", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Point Work", "HIGH"),
        CivilDuty("M25", "27.05.2026", "May 2026", "Amrish", "GVGN ACD automatic conflict detection measurement", "GVGN", "Mandi Gobindgarh New", "Km 1216.500", 30.6652, 76.3054, "Inspection", "HIGH"),

        // JUNE 2026
        CivilDuty("J01", "02.06.2026", "June 2026", "Gautam", "Point measurement at NSIR all points", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Point Work", "HIGH", "MONTHLY"),
        CivilDuty("J02", "02.06.2026", "June 2026", "Suraj", "Point measurement main line at CHAN yard", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Point Work", "HIGH"),
        CivilDuty("J03", "04.06.2026", "June 2026", "Gautam", "Inspection of cable with S&T and Elect at SMUN", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Inspection", "MEDIUM"),
        CivilDuty("J04", "04.06.2026", "June 2026", "Sunny", "Inspection of cable with S&T and Elect at NSIR", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Inspection", "MEDIUM"),
        CivilDuty("J05", "05.06.2026", "June 2026", "Amrish", "Curve measurement at curve no. 353, 354, 355", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Curve Measurement", "HIGH", "MONTHLY"),
        CivilDuty("J06", "10.06.2026", "June 2026", "Suraj", "KNNN point greasing work", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Maintenance", "MEDIUM", "WEEKLY"),
        CivilDuty("J07", "11.06.2026", "June 2026", "Sunny", "JPC at SMUN yard with Ranjeet & Tarsem", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Point Work", "HIGH"),
        CivilDuty("J08", "13.06.2026", "June 2026", "Gautam", "Motor trolley inspection KNNN to CHAN to SNL", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Inspection", "HIGH", "WEEKLY"),
        CivilDuty("J09", "16.06.2026", "June 2026", "Tarsem", "Deweeding work at SMUN yard near point 295B", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Maintenance", "LOW"),
        CivilDuty("J10", "19.06.2026", "June 2026", "Gautam", "Patrolling km 1167 - 1172", "SMUN", "Shambhu New", "Km 1169.000", 30.3472, 76.7125, "Inspection", "HIGH", "DAILY"),
        CivilDuty("J11", "20.06.2026", "June 2026", "Ramesh", "Greasing at SBJN yard", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Maintenance", "MEDIUM", "WEEKLY"),
        CivilDuty("J12", "23.06.2026", "June 2026", "Sunny", "108 SEJ board painting with Gautam", "RPJ", "Bridge 108", "Km 1174.500", 30.4842, 76.5930, "Maintenance", "LOW"),
        CivilDuty("J13", "24.06.2026", "June 2026", "Ranjeet", "FM painting at SMUN yard", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Maintenance", "LOW"),
        CivilDuty("J14", "25.06.2026", "June 2026", "Sunny", "115 SEJ board painting with Gautam", "RPJ", "Bridge 115", "Km 1175.500", 30.4842, 76.5930, "Maintenance", "LOW"),
        CivilDuty("J15", "29.06.2026", "June 2026", "Gautam", "SBJN gauge correction work", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Maintenance", "HIGH", "MONTHLY"),

        // JULY 2026
        CivilDuty("JL01", "01.07.2026", "July 2026", "Sunny", "SBJN gauge corrections", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Maintenance", "HIGH"),
        CivilDuty("JL02", "01.07.2026", "July 2026", "Gautam", "CHAN JPC at 8:00 AM", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Point Work", "HIGH", "WEEKLY"),
        CivilDuty("JL03", "01.07.2026", "July 2026", "Ranjeet", "Km board painting", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Maintenance", "LOW"),
        CivilDuty("JL04", "01.07.2026", "July 2026", "Suraj", "Grass cutting at km 1224", "KNNN", "Khanna New", "Km 1224.000", 30.7056, 76.2185, "Maintenance", "LOW"),
        CivilDuty("JL05", "03.07.2026", "July 2026", "Sunny", "Bridge no 114 rain cut repair", "RPJ", "Bridge 114", "Km 1175.000", 30.4842, 76.5930, "Maintenance", "HIGH"),
        CivilDuty("JL06", "04.07.2026", "July 2026", "Gautam", "SMUN loop line register update", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Inspection", "MEDIUM", "MONTHLY"),
        CivilDuty("JL07", "04.07.2026", "July 2026", "Ranjeet", "SMUN loop line measurement", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Inspection", "HIGH"),
        CivilDuty("JL08", "04.07.2026", "July 2026", "Sudhir", "Loop line measurement at KNNN yard", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Inspection", "HIGH"),
        CivilDuty("JL09", "07.07.2026", "July 2026", "Gautam", "SBJN motor trolley inspection", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Inspection", "HIGH"),
        CivilDuty("JL10", "13.07.2026", "July 2026", "Tarsem", "Guard rail work at km 1178/11-12 RPJ line", "RPJ", "Rajpura Link", "Km 1178.110", 30.4842, 76.5930, "Maintenance", "HIGH"),
        CivilDuty("JL11", "16.07.2026", "July 2026", "Suraj", "Curve measurement at Br no 170", "BR 165", "Major Bridge 165/170", "Km 1195.000", 30.5900, 76.4400, "Curve Measurement", "HIGH"),
        CivilDuty("JL12", "20.07.2026", "July 2026", "Suraj", "CHAN point maintenance work with gauge level", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Point Work", "HIGH", "WEEKLY"),
        CivilDuty("JL13", "27.07.2026", "July 2026", "Sunny", "Rail loading work by RBMV machine at Br 120", "RPJ", "Bridge 120", "Km 1176.000", 30.4842, 76.5930, "Operations", "HIGH"),
        CivilDuty("JL14", "29.07.2026", "July 2026", "Sunny", "Bridge no 120 guard rail installation work", "RPJ", "Bridge 120", "Km 1176.000", 30.4842, 76.5930, "Maintenance", "HIGH"),
        CivilDuty("JL15", "31.07.2026", "July 2026", "Tarsem", "Br 108 bridge inspection team with Abhay Maurya", "RPJ", "Bridge 108", "Km 1174.500", 30.4842, 76.5930, "Inspection", "HIGH"),

        // AUGUST 2026
        CivilDuty("AG01", "01.08.2026", "August 2026", "Sunny", "1208/4 packing work", "NSIR", "New Sirhind", "Km 1208.400", 30.6354, 76.3881, "Maintenance", "HIGH"),
        CivilDuty("AG02", "01.08.2026", "August 2026", "Dayal singh", "Bridge maintenance at 165", "BR 165", "Major Bridge 165/170", "Km 1195.000", 30.5900, 76.4400, "Maintenance", "HIGH"),
        CivilDuty("AG03", "01.08.2026", "August 2026", "Swarn singh", "Bridge maintenance at 165", "BR 165", "Major Bridge 165/170", "Km 1195.000", 30.5900, 76.4400, "Maintenance", "HIGH"),
        CivilDuty("AG04", "03.08.2026", "August 2026", "Gautam", "JPC joint check at SBJN", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Point Work", "HIGH", "MONTHLY"),
        CivilDuty("AG05", "03.08.2026", "August 2026", "Suraj", "JPC at KNNN yard", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Point Work", "HIGH", "MONTHLY"),
        CivilDuty("AG06", "06.08.2026", "August 2026", "Sunny", "Bridge 120 guard rail installation work", "RPJ", "Bridge 120", "Km 1176.000", 30.4842, 76.5930, "Maintenance", "HIGH"),
        CivilDuty("AG07", "06.08.2026", "August 2026", "Suraj", "Curve measurement curve no 365 km 1222/500", "GVGN", "Mandi Gobindgarh New", "Km 1222.500", 30.6652, 76.3054, "Curve Measurement", "HIGH", "MONTHLY"),
        CivilDuty("AG08", "10.08.2026", "August 2026", "Sunny", "SBJN point measurement", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Point Work", "HIGH"),
        CivilDuty("AG09", "11.08.2026", "August 2026", "Sudhir", "Bridge maintenance work at Br 199", "CHAN", "Chawa Pail New", "Km 1239.500", 30.7550, 76.1052, "Maintenance", "HIGH"),
        CivilDuty("AG10", "13.08.2026", "August 2026", "Dayal singh", "LC 164 gate maintenance", "LC 164", "Level Crossing 164", "Km 1233.500", 30.7300, 76.1600, "Maintenance", "HIGH"),
        CivilDuty("AG11", "14.08.2026", "August 2026", "Tarsem", "Office p-way material counting at SMUN", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Inspection", "MEDIUM"),
        CivilDuty("AG12", "17.08.2026", "August 2026", "Gautam", "Bridge 165 packing work", "BR 165", "Major Bridge 165/170", "Km 1195.000", 30.5900, 76.4400, "Maintenance", "HIGH"),
        CivilDuty("AG13", "18.08.2026", "August 2026", "Sudhir", "Packing at km 1233/6-7 and 1234/17", "KNNN", "Khanna New", "Km 1233.600", 30.7056, 76.2185, "Maintenance", "HIGH"),
        CivilDuty("AG14", "24.08.2026", "August 2026", "Amrish", "Bridge 165 and 170 cross level gauge attend", "BR 165", "Major Bridge 165/170", "Km 1195.000", 30.5900, 76.4400, "Inspection", "HIGH"),
        CivilDuty("AG15", "26.08.2026", "August 2026", "Suraj", "LC 164 with gauge level with staff team", "LC 164", "Level Crossing 164", "Km 1233.500", 30.7300, 76.1600, "Inspection", "HIGH", "WEEKLY"),

        // SEPTEMBER 2026
        CivilDuty("S01", "01.09.2026", "September 2026", "Ranjeet", "SBJN for point work", "SBJN", "Sarai Banjara New", "Km 1187.130", 30.5501, 76.4952, "Point Work", "HIGH", "WEEKLY"),
        CivilDuty("S02", "01.09.2026", "September 2026", "Suraj", "Point greasing GVGN yard", "GVGN", "Mandi Gobindgarh New", "Km 1216.500", 30.6652, 76.3054, "Maintenance", "MEDIUM", "WEEKLY"),
        CivilDuty("S03", "01.09.2026", "September 2026", "Sudhir", "Bhola gang deweeding work", "KNNN", "Khanna New", "Km 1224.000", 30.7056, 76.2185, "Maintenance", "LOW"),
        CivilDuty("S04", "01.09.2026", "September 2026", "Rahul", "NSIR for greasing", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Maintenance", "MEDIUM", "WEEKLY"),
        CivilDuty("S05", "01.09.2026", "September 2026", "Amit", "Point greasing KNNN yard", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Maintenance", "MEDIUM", "WEEKLY"),
        CivilDuty("S06", "02.09.2026", "September 2026", "Gautam", "CHAN for gauge and diesel check", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Operations", "HIGH"),
        CivilDuty("S07", "03.09.2026", "September 2026", "Suraj", "Point measurement at CHAN yard", "CHAN", "Chawa Pail New", "Km 1238.800", 30.7550, 76.1052, "Point Work", "HIGH"),
        CivilDuty("S08", "05.09.2026", "September 2026", "Gautam", "LC 151 inspection", "KNNN", "Khanna New (LC 151)", "Km 1228.000", 30.7056, 76.2185, "Inspection", "HIGH"),
        CivilDuty("S09", "07.09.2026", "September 2026", "Gautam", "Motor trolley KNNN with Suraj & Amrish", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Inspection", "HIGH"),
        CivilDuty("S10", "07.09.2026", "September 2026", "Ranjeet", "SMUN for JPC point inspection", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Point Work", "HIGH", "MONTHLY"),
        CivilDuty("S11", "08.09.2026", "September 2026", "Gautam", "NSIR for packing work", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Maintenance", "HIGH"),
        CivilDuty("S12", "10.09.2026", "September 2026", "Gautam", "SMUN JPC joint point check", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Point Work", "HIGH", "MONTHLY"),
        CivilDuty("S13", "10.09.2026", "September 2026", "Ranjeet", "USFD team testing", "RPJ", "Rajpura Link Line", "Km 1175.500", 30.4842, 76.5930, "Inspection", "HIGH"),
        CivilDuty("S14", "11.09.2026", "September 2026", "Gautam", "Motor trolley NSIR", "NSIR", "New Sirhind", "Km 1205.700", 30.6354, 76.3881, "Inspection", "HIGH"),
        CivilDuty("S15", "14.09.2026", "September 2026", "Suraj", "Curve painting and numbering", "GVGN", "Mandi Gobindgarh New", "Km 1222.000", 30.6652, 76.3054, "Maintenance", "MEDIUM"),
        CivilDuty("S16", "14.09.2026", "September 2026", "Sudhir", "Bhola gang deweeding work Doraha to CHAN st", "CHAN", "Chawa Pail New (Doraha)", "Km 1242.000", 30.7550, 76.1052, "Maintenance", "LOW"),
        CivilDuty("S17", "16.09.2026", "September 2026", "Ranjeet", "USFD testing at RPJ link line", "RPJ", "Rajpura Link Line", "Km 1175.500", 30.4842, 76.5930, "Inspection", "HIGH"),
        CivilDuty("S18", "18.09.2026", "September 2026", "Suraj", "ACD measurement at KNNN and CHAN", "KNNN", "Khanna New", "Km 1225.400", 30.7056, 76.2185, "Inspection", "HIGH"),
        CivilDuty("S19", "19.09.2026", "September 2026", "Suraj", "Caution board installation time 7:00", "SMUN", "Shambhu New", "Km 1167.210", 30.3472, 76.7125, "Operations", "HIGH"),
        CivilDuty("S20", "21.09.2026", "September 2026", "Dayal singh", "Bridge 165 and 170 maintenance", "BR 165", "Major Bridge 165/170", "Km 1195.000", 30.5900, 76.4400, "Maintenance", "HIGH")
    )

    fun getStaffNames(): List<String> {
        return allDuties.map { it.staffName }.distinct().sorted()
    }

    fun getStations(): List<String> {
        return allDuties.map { it.stationCode }.distinct().sorted()
    }

    fun getMonths(): List<String> {
        return listOf("All Months", "May 2026", "June 2026", "July 2026", "August 2026", "September 2026")
    }

    fun filterDuties(staff: String?, station: String?, month: String?, search: String = ""): List<CivilDuty> {
        return allDuties.filter { duty ->
            val matchStaff = staff == null || staff == "All Staff" || duty.staffName.equals(staff, ignoreCase = true)
            val matchStation = station == null || station == "All Stations" || duty.stationCode.equals(station, ignoreCase = true)
            val matchMonth = month == null || month == "All Months" || duty.month.equals(month, ignoreCase = true)
            val matchQuery = search.isBlank() ||
                    duty.activity.contains(search, ignoreCase = true) ||
                    duty.staffName.contains(search, ignoreCase = true) ||
                    duty.stationName.contains(search, ignoreCase = true) ||
                    duty.kmMarker.contains(search, ignoreCase = true)

            matchStaff && matchStation && matchMonth && matchQuery
        }
    }

    fun dutyToGeoTask(duty: CivilDuty, recurrenceOverride: String? = null): GeoTask {
        val recurrence = recurrenceOverride ?: duty.defaultRecurrence
        val isRec = recurrence != "NONE"

        return GeoTask(
            title = "[${duty.stationCode}] ${duty.activity}",
            description = "Staff: ${duty.staffName} | Section: SMUN-SNL | Location: ${duty.stationName} (${duty.kmMarker}) | Date: ${duty.date}",
            locationName = "${duty.stationName} (${duty.kmMarker})",
            latitude = duty.latitude,
            longitude = duty.longitude,
            radiusMeters = 300f,
            triggerCondition = "ON_ARRIVAL",
            isVoicePushEnabled = true,
            priority = duty.priority,
            category = duty.category,
            dueDateEpoch = System.currentTimeMillis() + 86400_000L,
            source = "ROSTER_DUTY",
            isRecurring = isRec,
            recurrenceType = recurrence,
            recurrenceInterval = 1
        )
    }
}
