package gov.ca.cwds.rest.api.domain.auth;

import gov.ca.cwds.data.ApiSysCodeAware;

import java.util.HashMap;
import java.util.Map;


/**
 * Enumerated types for GovernmentEntityType, for FKS_META_T =GVR_ENTC.
 *
 * @author CWDS API Team
 */
public enum GovernmentEntityType implements ApiSysCodeAware {

  /**
   * None
   */
  NONE(0, "None", "0"),

  /**
   * Alameda
   */
  ALAMEDA(1068, "Alameda", "1"),

  /**
   * Alpine
   */
  ALPINE(1069, "Alpine", "2"),

  /**
   * Amador
   */
  AMADOR(1070, "Amador", "3"),

  /**
   * Butte
   */
  BUTTE(1071, "Butte", "4"),

  /**
   * Calaveras
   */
  CALAVERAS(1072, "Calaveras", "5"),

  /**
   * Colusa
   */
  COLUSA(1073, "Colusa", "6"),

  /**
   * Contra Costa
   */
  CONTRA_COSTA(1074, "Contra Costa", "7"),

  /**
   * Del Norte
   */
  DEL_NORTE(1075, "Del Norte", "8"),

  /**
   * El Dorado
   */
  EL_DORADO(1076, "El Dorado", "9"),

  /**
   * Fresno
   */
  FRESNO(1077, "Fresno", "10"),

  /**
   * Glenn
   */
  GLENN(1078, "Glenn", "11"),

  /**
   * Humboldt
   */
  HUMBOLDT(1079, "Humboldt", "12"),

  /**
   * Imperial
   */
  IMPERIAL(1080, "Imperial", "13"),

  /**
   * Inyo
   */
  INYO(1081, "Inyo", "14"),

  /**
   * Kern
   */
  KERN(1082, "Kern", "15"),

  /**
   * Kings
   */
  KINGS(1083, "Kings", "16"),

  /**
   * Lake
   */
  LAKE(1084, "Lake", "17"),

  /**
   * Lassen
   */
  LASSEN(1085, "Lassen", "18"),

  /**
   * Los Angeles
   */
  LOS_ANGELES(1086, "Los Angeles", "19"),

  /**
   * Madera
   */
  MADERA(1087, "Madera", "20"),

  /**
   * Marin
   */
  MARIN(1088, "Marin", "21"),

  /**
   * Mariposa
   */
  MARIPOSA(1089, "Mariposa", "22"),

  /**
   * Mendocino
   */
  MENDOCINO(1090, "Mendocino", "23"),

  /**
   * Merced
   */
  MERCED(1091, "Merced", "24"),

  /**
   * Modoc
   */
  MODOC(1092, "Modoc", "25"),

  /**
   * Mono
   */
  MONO(1093, "Mono", "26"),

  /**
   * Monterey
   */
  MONTEREY(1094, "Monterey", "27"),

  /**
   * Napa
   */
  NAPA(1095, "Napa", "28"),

  /**
   * Nevada
   */
  NEVADA(1096, "Nevada", "29"),

  /**
   * Orange
   */
  ORANGE(1097, "Orange", "30"),

  /**
   * Placer
   */
  PLACER(1098, "Placer", "31"),

  /**
   * Plumas
   */
  PLUMAS(1099, "Plumas", "32"),

  /**
   * Riverside
   */
  RIVERSIDE(1100, "Riverside", "33"),

  /**
   * Sacramento
   */
  SACRAMENTO(1101, "Sacramento", "34"),

  /**
   * San Benito
   */
  SAN_BENITO(1102, "San Benito", "35"),

  /**
   * San Bernardino
   */
  SAN_BERNARDINO(1103, "San Bernardino", "36"),

  /**
   * San Diego
   */
  SAN_DIEGO(1104, "San Diego", "37"),

  /**
   * San Francisco
   */
  SAN_FRANCISCO(1105, "San Francisco", "38"),

  /**
   * San Joaquin
   */
  SAN_JOAQUIN(1106, "San Joaquin", "39"),

  /**
   * San Luis Obispo
   */
  SAN_LUIS_OBISPO(1107, "San Luis Obispo", "40"),

  /**
   * San Mateo
   */
  SAN_MATEO(1108, "San Mateo", "41"),

  /**
   * Santa Barbara
   */
  SANTA_BARBARA(1109, "Santa Barbara", "42"),

  /**
   * Santa Clara
   */
  SANTA_CLARA(1110, "Santa Clara", "43"),

  /**
   * Santa Cruz
   */
  SANTA_CRUZ(1111, "Santa Cruz", "44"),

  /**
   * Shasta
   */
  SHASTA(1112, "Shasta", "45"),

  /**
   * Sierra
   */
  SIERRA(1113, "Sierra", "46"),

  /**
   * Siskiyou
   */
  SISKIYOU(1114, "Siskiyou", "47"),

  /**
   * Solano
   */
  SOLANO(1115, "Solano", "48"),

  /**
   * Sonoma
   */
  SONOMA(1116, "Sonoma", "49"),

  /**
   * Stanislaus
   */
  STANISLAUS(1117, "Stanislaus", "50"),

  /**
   * Sutter
   */
  SUTTER(1118, "Sutter", "51"),

  /**
   * Tehama
   */
  TEHAMA(1119, "Tehama", "52"),

  /**
   * Trinity
   */
  TRINITY(1120, "Trinity", "53"),

  /**
   * Tulare
   */
  TULARE(1121, "Tulare", "54"),

  /**
   * Tuolumne
   */
  TUOLUMNE(1122, "Tuolumne", "55"),

  /**
   * Ventura
   */
  VENTURA(1123, "Ventura", "56"),

  /**
   * Yolo
   */
  YOLO(1124, "Yolo", "57"),

  /**
   * Yuba
   */
  YUBA(1125, "Yuba", "58"),

  /**
   * State of California
   */
  STATE_OF_CALIFORNIA(1126, "State of California", "99");

  private final int sysId;
  private final String description;
  private final String countyCd;

  private GovernmentEntityType(int sysId, String shortDsc, String countyCd) {
    this.sysId = sysId;
    this.description = shortDsc;
    this.countyCd = countyCd;
  }

  @Override
  public int getSysId() {
    return this.sysId;
  }


  @Override
  public String getDescription() {
    return description;
  }

  public String getCountyCd() {
    return countyCd;
  }

  @Override
  public ApiSysCodeAware lookupBySysId(int sysId) {
    return mapBySysId.get(sysId);
  }

  public static GovernmentEntityType findBySysId(int sysId) {
    return mapBySysId.containsKey(sysId) ? mapBySysId.get(sysId): GovernmentEntityType.NONE;
  }

  public static GovernmentEntityType findByCountyCd(String countyCd) {
    return mapByCountyCd.containsKey(countyCd) ? mapByCountyCd.get(countyCd)
            : GovernmentEntityType.NONE;
  }


  private static final Map<Integer, GovernmentEntityType> mapBySysId = new HashMap<>();
  private static final Map<String, GovernmentEntityType> mapByCountyCd = new HashMap<>();


  static {
    for (GovernmentEntityType e : GovernmentEntityType.values()) {
      mapBySysId.put(e.sysId, e);
      mapByCountyCd.put(e.countyCd, e);
    }
  }

}
