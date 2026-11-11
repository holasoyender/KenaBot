@echo off
@rem ####################################
@rem # Generate docker image for Kena-API
@rem ####################################

@rem
@rem Copyright 2024 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.

@rem # Docker image build

set /p branch=Branch:
echo Building Kena-API image for branch "%branch%"... 
cd api && docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/holasoyender/kenabot-api:%branch% --push .
