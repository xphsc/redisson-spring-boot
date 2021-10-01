/*
 * Copyright (c) 2021 huipei.x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.xphsc.redisson.core.ratelimiter.entity;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public enum DurationStyle {
    /**
     *
     */
    SIMPLE("^([\\+\\-]?\\d+)([a-zA-Z]{0,2})$") {
        @Override
        public Duration parse(String value, ChronoUnit unit) {
            try {
                Matcher matcher = this.matcher(value);
                Assert.state(matcher.matches(), "Does not match simple duration pattern");
                String suffix = matcher.group(2);
                return (StringUtils.hasLength(suffix) ? Unit.fromSuffix(suffix) : Unit.fromChronoUnit(unit)).parse(matcher.group(1));
            } catch (Exception var5) {
                throw new IllegalArgumentException("'" + value + "' is not a valid simple duration", var5);
            }
        }

        @Override
        public String print(Duration value, ChronoUnit unit) {
            return Unit.fromChronoUnit(unit).print(value);
        }
    },
    ISO8601("^[\\+\\-]?P.*$") {
        @Override
        public Duration parse(String value, ChronoUnit unit) {
            try {
                return Duration.parse(value);
            } catch (Exception var4) {
                throw new IllegalArgumentException("'" + value + "' is not a valid ISO-8601 duration", var4);
            }
        }

        @Override
        public String print(Duration value, ChronoUnit unit) {
            return value.toString();
        }
    };

    private final Pattern pattern;

    private DurationStyle(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    protected final boolean matches(String value) {
        return this.pattern.matcher(value).matches();
    }

    protected final Matcher matcher(String value) {
        return this.pattern.matcher(value);
    }

    public Duration parse(String value) {
        return this.parse(value, (ChronoUnit)null);
    }

    public abstract Duration parse(String value, ChronoUnit unit);

    public String print(Duration value) {
        return this.print(value, (ChronoUnit)null);
    }

    public abstract String print(Duration value, ChronoUnit unit);

    public static Duration detectAndParse(String value) {
        return detectAndParse(value, (ChronoUnit)null);
    }

    public static Duration detectAndParse(String value, ChronoUnit unit) {
        return detect(value).parse(value, unit);
    }

    public static DurationStyle detect(String value) {
        Assert.notNull(value, "Value must not be null");
      DurationStyle[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            DurationStyle candidate = var1[var3];
            if (candidate.matches(value)) {
                return candidate;
            }
        }

        throw new IllegalArgumentException("'" + value + "' is not a valid duration");
    }

    static enum Unit {
        /**
         *
         */
        NANOS(ChronoUnit.NANOS, "ns", Duration::toNanos),
        MILLIS(ChronoUnit.MILLIS, "ms", Duration::toMillis),
        SECONDS(ChronoUnit.SECONDS, "s", Duration::getSeconds),
        MINUTES(ChronoUnit.MINUTES, "m", Duration::toMinutes),
        HOURS(ChronoUnit.HOURS, "h", Duration::toHours),
        DAYS(ChronoUnit.DAYS, "d", Duration::toDays);

        private final ChronoUnit chronoUnit;
        private final String suffix;
        private Function<Duration, Long> longValue;

        private Unit(ChronoUnit chronoUnit, String suffix, Function<Duration, Long> toUnit) {
            this.chronoUnit = chronoUnit;
            this.suffix = suffix;
            this.longValue = toUnit;
        }

        public Duration parse(String value) {
            return Duration.of(Long.valueOf(value), this.chronoUnit);
        }

        public String print(Duration value) {
            return this.longValue(value) + this.suffix;
        }

        public long longValue(Duration value) {
            return (Long)this.longValue.apply(value);
        }

        public static Unit fromChronoUnit(ChronoUnit chronoUnit) {
            if (chronoUnit == null) {
                return MILLIS;
            } else {
               Unit[] var1 = values();
                int var2 = var1.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                   Unit candidate = var1[var3];
                    if (candidate.chronoUnit == chronoUnit) {
                        return candidate;
                    }
                }

                throw new IllegalArgumentException("Unknown unit " + chronoUnit);
            }
        }

        public static Unit fromSuffix(String suffix) {
          Unit[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
              Unit candidate = var1[var3];
                if (candidate.suffix.equalsIgnoreCase(suffix)) {
                    return candidate;
                }
            }

            throw new IllegalArgumentException("Unknown unit '" + suffix + "'");
        }
    }
}
