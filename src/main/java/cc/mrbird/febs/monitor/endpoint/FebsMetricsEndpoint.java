package cc.mrbird.febs.monitor.endpoint;

import cc.mrbird.febs.common.annotation.FebsEndPoint;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.springframework.boot.actuate.endpoint.InvalidEndpointRequestException;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author MrBird
 */
@FebsEndPoint
public class FebsMetricsEndpoint {

    //springboot整合一个meter进行数据收集和监控
    private final MeterRegistry registry;

    public FebsMetricsEndpoint(MeterRegistry registry) {
        this.registry = registry;
    }

    public ListNamesResponse listNames() {
        Set<String> names = new LinkedHashSet<>();
        collectNames(names, this.registry);
        return new ListNamesResponse(names);
    }

    public FebsMetricResponse metric(String requiredMetricName, List<String> tag) {
        List<Tag> tags = parseTags(tag);
        //1.获取meter
        Collection<Meter> meters = findFirstMatchingMeters(this.registry,
                requiredMetricName, tags);
        if (meters.isEmpty()) {
            return null;
        }
        //2.获取samples样本
        Map<Statistic, Double> samples = getSamples(meters);
        //3.获取可用的tags
        Map<String, Set<String>> availableTags = getAvailableTags(meters);
        tags.forEach((t) -> availableTags.remove(t.getKey()));
        Meter.Id meterId = meters.iterator().next().getId();
        return new FebsMetricResponse(requiredMetricName, meterId.getDescription(),
                meterId.getBaseUnit(), asList(samples, Sample::new),
                asList(availableTags, AvailableTag::new));
    }

    private void collectNames(Set<String> names, MeterRegistry registry) {
        if (registry instanceof CompositeMeterRegistry) {
            //获取所有的注册器（复合meter注册器）递归
            ((CompositeMeterRegistry) registry).getRegistries()
                    .forEach((member) -> collectNames(names, member));
        } else {
            //调用本类方法进行映射转换
            registry.getMeters().stream().map(this::getName).forEach(names::add);
        }
    }

    private String getName(Meter meter) {
        //meter Id（name + tags）
        return meter.getId().getName();
    }

    private List<Tag> parseTags(List<String> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream().map(this::parseTag).collect(Collectors.toList());
    }

    private Tag parseTag(String tag) {
        String[] parts = tag.split(":", 2);
        if (parts.length != 2) {
            throw new InvalidEndpointRequestException(
                    "Each tag parameter must be in the form 'key:value' but was: " + tag,
                    "Each tag parameter must be in the form 'key:value'");
        }
        return Tag.of(parts[0], parts[1]);
    }

    //获取第一个匹配的tag
    private Collection<Meter> findFirstMatchingMeters(MeterRegistry registry, String name,
                                                      Iterable<Tag> tags) {
        if (registry instanceof CompositeMeterRegistry) {
            return findFirstMatchingMeters((CompositeMeterRegistry) registry, name, tags);
        }
        //在注册器中进行寻找，name | tags
        return registry.find(name).tags(tags).meters();
    }

    private Collection<Meter> findFirstMatchingMeters(CompositeMeterRegistry composite,
                                                      String name, Iterable<Tag> tags) {
        return composite.getRegistries().stream()
                .map((registry) -> findFirstMatchingMeters(registry, name, tags))
                .filter((matching) -> !matching.isEmpty()).findFirst()
                .orElse(Collections.emptyList());
    }

    //获取所有样本
    private Map<Statistic, Double> getSamples(Collection<Meter> meters) {
        Map<Statistic, Double> samples = new LinkedHashMap<>();
        meters.forEach((meter) -> mergeMeasurements(samples, meter));
        return samples;
    }

    private void mergeMeasurements(Map<Statistic, Double> samples, Meter meter) {
        meter.measure().forEach((measurement) -> samples.merge(measurement.getStatistic(),
                measurement.getValue(), mergeFunction(measurement.getStatistic())));
    }

    //如果统计是最大的，就取最大的，否则将累加求和
    private BiFunction<Double, Double, Double> mergeFunction(Statistic statistic) {
        return Statistic.MAX.equals(statistic) ? Double::max : Double::sum;
    }

    //取出meters中的可用标签，【集合的单例传递】
    private Map<String, Set<String>> getAvailableTags(Collection<Meter> meters) {
        Map<String, Set<String>> availableTags = new HashMap<>();
        meters.forEach((meter) -> mergeAvailableTags(availableTags, meter));
        return availableTags;
    }

    //这里就是tag -->key value
    private void mergeAvailableTags(Map<String, Set<String>> availableTags, Meter meter) {
        meter.getId().getTags().forEach((tag) -> {
            Set<String> value = Collections.singleton(tag.getValue());
            availableTags.merge(tag.getKey(), value, this::merge);
        });
    }

    private <T> Set<T> merge(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1.size() + set2.size());
        result.addAll(set1);
        result.addAll(set2);
        return result;
    }

    //泛型方法要包含所有的泛型定义
    private <K, V, T> List<T> asList(Map<K, V> map, BiFunction<K, V, T> mapper) {
        return map.entrySet().stream()
                .map((entry) -> mapper.apply(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    //列表响应名
    public static final class ListNamesResponse {

        private final Set<String> names;

        ListNamesResponse(Set<String> names) {
            this.names = names;
        }

        public Set<String> getNames() {
            return this.names;
        }

    }

    //metric的响应（样本 + 可用标签）
    public static final class FebsMetricResponse {

        private final String name;

        private final String description;

        private final String baseUnit;

        private final List<Sample> measurements;

        private final List<AvailableTag> availableTags;

        FebsMetricResponse(String name, String description, String baseUnit,
                           List<Sample> measurements, List<AvailableTag> availableTags) {
            this.name = name;
            this.description = description;
            this.baseUnit = baseUnit;
            this.measurements = measurements;
            this.availableTags = availableTags;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public String getBaseUnit() {
            return this.baseUnit;
        }

        public List<Sample> getMeasurements() {
            return this.measurements;
        }

        public List<AvailableTag> getAvailableTags() {
            return this.availableTags;
        }

    }

    //可用的标签tag
    public static final class AvailableTag {

        private final String tag;

        private final Set<String> values;

        AvailableTag(String tag, Set<String> values) {
            this.tag = tag;
            this.values = values;
        }

        public String getTag() {
            return this.tag;
        }

        public Set<String> getValues() {
            return this.values;
        }

    }

    //样本（统计）
    public static final class Sample {

        private final Statistic statistic;

        private final Double value;

        Sample(Statistic statistic, Double value) {
            this.statistic = statistic;
            this.value = value;
        }

        public Statistic getStatistic() {
            return this.statistic;
        }

        public Double getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return "MeasurementSample{" + "statistic=" + this.statistic + ", value="
                    + this.value + '}';
        }

    }

}
