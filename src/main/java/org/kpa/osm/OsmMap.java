package org.kpa.osm;

import com.google.common.base.MoreObjects;
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.*;
import de.westnordost.osmapi.map.handler.DefaultMapDataHandler;
import org.kpa.sonar.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.kpa.sonar.model.Modeller.loadTracks;

public class OsmMap {
    private static final Logger logger = LoggerFactory.getLogger(OsmMap.class);
    private final BoundingBox bounds;
    private final Map<Long, Way> ways = new HashMap<>();
    private final Map<Long, Node> nodes = new HashMap<>();
    private final String cacheDir;
    private List<Relation> riverBanks;

    public OsmMap(Point swPoint, Point nePoint, String cacheDir) {
        this.cacheDir = cacheDir;
        bounds = new BoundingBox(
                swPoint.getLattitude().doubleValue(), swPoint.getLongitude().doubleValue(),
                nePoint.getLattitude().doubleValue(), nePoint.getLongitude().doubleValue());
    }

    private static Map<String, String> tagWrap(Map<String, String> tags) {
        return new LinkedHashMap<>(MoreObjects.firstNonNull(tags, Collections.emptyMap()));
    }


    public String run() {
        riverBanks = filter(bounds, Relation.class,
                rel -> rel.getTags() != null && "riverbank".equals(rel.getTags().get("waterway")));
        riverBanks.forEach(rel -> rel.getMembers().forEach(member -> ways.put(member.getRef(), null)));
        lookup(bounds, Way.class, ways);
        ways.values().stream().filter(Objects::nonNull).forEach(val -> val.getNodeIds().forEach(id -> nodes.put(id, null)));
        lookup(bounds, Node.class, nodes);
//        riverBanks.forEach(relation -> {
//            logger.info("River bank: {} with tags {}", relation.getId(), tagWrap(relation.getTags()));
//            relation.getMembers().forEach(member -> {
//                Way way = ways.get(member.getRef());
//                if (way == null) {
//                    logger.info("\tWay {} {} not found", member.getRef(), member.getRole());
//                } else {
//                    logger.info("\tWay {} {} with tags {}", way.getId(), member.getRole(), tagWrap(way.getTags()));
//                    way.getNodeIds().forEach(id -> {
//                        Node bankPoint = nodes.get(id);
//                        if (bankPoint == null) {
//                            logger.info("\t\tLat:Lon= {empty}");
//                        } else {
//                            logger.info("\t\t{} Lat:Lon= {},{}", id, bankPoint.getPosition().getLatitude(), bankPoint.getPosition().getLongitude());
//                        }
//                    });
//                }
//            });
//        });
        return "";
    }

    public Map<Long, Node> getNodes() {
        return nodes;
    }

    public <T extends Element> void lookup(BoundingBox bounds, Class<T> clazz, Map<Long, T> mapById) {
        List<T> map = filter(bounds, clazz, way -> mapById.containsKey(way.getId()));
        map.forEach(val -> mapById.put(val.getId(), (T) val));
    }

    public <T extends Element> List<T> filter(BoundingBox bounds, Class<T> clazz, Function<T, Boolean> filter) {
        List<T> result = new ArrayList<>();
        scan(bounds, clazz, way -> {
            if (filter.apply(way)) result.add(way);
        });
        return result;
    }

    public <T extends Element> void scan(BoundingBox bounds, Class<T> clazz, Consumer<T> consumer) {
        CachedOsmConnection connection = new CachedOsmConnection("https://api.osm.org/api/0.6/", "");
        new File("./cache/").mkdir();
        connection.setCacheDirectory(cacheDir);
        MapDataDao mapDataDao = new MapDataDao(connection);
        mapDataDao.getMap(bounds,
                new DefaultMapDataHandler() {
                    @Override
                    public void handle(Relation relation) {
                        if (clazz == null || clazz.isAssignableFrom(Relation.class)) {
                            consumer.accept((T) relation);
                        }
                    }

                    @Override
                    public void handle(Node node) {
                        if (clazz == null || clazz.isAssignableFrom(Node.class)) {
                            consumer.accept((T) node);
                        }
                    }

                    @Override
                    public void handle(Way way) {
                        if (clazz == null || clazz.isAssignableFrom(Way.class)) {
                            consumer.accept((T) way);
                        }
                    }
                });
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        loadTracks(2);
    }

}
