
# Makefile
# see https://www.gnu.org/software/make/manual/make.html


# VARIABLES

NAME := kafka-commons
GIT_COMMIT_HASH := $(shell git log --pretty=format:'%h' -n 1)

BUILD_TOOL := ./mvnw
JAR_FILE := $(shell find build/libs -name '*.jar' 2>/dev/null)

.PHONY: help
.DEFAULT_GOAL := help


# GENERAL

help :		## Help
	@echo ""
	@echo "*** $(NAME) Makefile help ***"
	@echo ""
	@echo "Targets list:"
	@grep -E '^[a-zA-Z_-]+ :.*?## .*$$' $(MAKEFILE_LIST) | sort -k 1,1 | awk 'BEGIN {FS = ":.*?## "}; {printf "\t\033[36m%-30s\033[0m %s\n", $$1, $$2}'
	@echo ""

print-variables :		## Print variables values
	@echo "MAKE: $(MAKE)"
	@echo "MAKEFILES: $(MAKEFILES)"
	@echo "MAKEFILE_LIST: $(MAKEFILE_LIST)"
	@echo "- - - "
	@echo "NAME: $(NAME)"
	@echo "GIT_COMMIT_HASH: $(GIT_COMMIT_HASH)"
	@echo "- - - "
	@echo "BUILD_TOOL: $(BUILD_TOOL)"
	@echo "JAR_FILE: $(JAR_FILE)"


# BUILDING

build :		## Build the library excluding tests
	$(BUILD_TOOL) package -DskipTests

build-test :		## Build the library including tests
	$(BUILD_TOOL) package

clean :		## Clean the library
	$(BUILD_TOOL) clean

clean-build :		## Clean and Build the library excluding tests
	$(BUILD_TOOL) clean package -DskipTests

clean-build-test :		## Clean and Build the library including tests
	$(BUILD_TOOL) clean package

install : clean-build-test		## Install the library into maven repo
	$(BUILD_TOOL) install


# TESTING

test : clean-build		## Run all tests in the library
	$(BUILD_TOOL) test

unit-test : clean-build		## Run Unit tests in the library
	$(BUILD_TOOL) test

integration-test : clean-build		## Run Integration tests in the library
	$(BUILD_TOOL) -Dtest=*IntegrationTest test

specific-test : clean-build		## Run Specific tests in the library
	$(BUILD_TOOL) -Dtest=$@ test
