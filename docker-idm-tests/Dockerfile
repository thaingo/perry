FROM cwds/javajdk

# Performance tests
RUN yum -y install wget
RUN cd /opt/ \
    && wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-4.0.tgz \
    && tar -xvzf apache-jmeter-4.0.tgz \
    && rm apache-jmeter-4.0.tgz
ENV JMETER_HOME /opt/apache-jmeter-4.0/
RUN mkdir /opt/idm-api-perf-test
ENV JMETER_TESTS /opt/idm-api-perf-test
RUN mkdir $JMETER_TESTS/jmeter
ADD jmeter $JMETER_TESTS/jmeter
RUN mkdir $JMETER_TESTS/results && mkdir $JMETER_TESTS/results/api && mkdir $JMETER_TESTS/results/rails
ADD entrypoint.sh /opt/
RUN chmod +x /opt/entrypoint.sh
WORKDIR /opt
CMD ["/opt/entrypoint.sh"]
