const { Document, Packer, Paragraph, TextRun, HeadingLevel, AlignmentType } = require('docx');
const fs = require('fs');

// 创建文档
const doc = new Document({
  styles: {
    default: { document: { run: { font: "SimSun", size: 21 } } },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 28, bold: true, font: "SimHei" },
        paragraph: { spacing: { before: 240, after: 240 }, alignment: AlignmentType.CENTER, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 24, bold: true, font: "SimHei" },
        paragraph: { spacing: { before: 180, after: 180 }, outlineLevel: 1 } },
      { id: "Heading3", name: "Heading 3", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 22, bold: true, font: "SimHei" },
        paragraph: { spacing: { before: 120, after: 120 }, outlineLevel: 2 } },
    ]
  },
  sections: [{
    properties: {
      page: {
        size: { width: 11906, height: 16838 },
        margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
      }
    },
    children: [
      // 标题
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        children: [new TextRun({ text: "本科毕业论文（设计）开题报告", bold: true, size: 44 })]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      // 题目
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [
          new TextRun({ text: "题    目    ", bold: true, size: 22 }),
          new TextRun({ text: "无人机交通巡检系统操作安全审计平台", bold: true, size: 22 })
        ]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      // 学生信息
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [
          new TextRun({ text: "学生姓名", size: 20 }),
          new TextRun({ text: "张庆晗", size: 20 }),
          new TextRun({ text: "    学    号", size: 20 }),
          new TextRun({ text: "20223000095", size: 20 })
        ]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [
          new TextRun({ text: "学    院", size: 20 }),
          new TextRun({ text: "网络空间安全学院 (密码学院)", size: 20 }),
          new TextRun({ text: "    专业年级", size: 20 }),
          new TextRun({ text: "22级信息安全", size: 20 })
        ]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [
          new TextRun({ text: "指导教师", size: 20 }),
          new TextRun({ text: "周腾", size: 20 }),
          new TextRun({ text: "    职称、学历", size: 20 }),
          new TextRun({ text: "教授", size: 20 })
        ]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [
          new TextRun({ text: "计划完成时间", size: 20 }),
          new TextRun({ text: "2026年5月30日", size: 20 })
        ]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 40 })] }),

      // 一、选题的目的、意义和国内外研究概况
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun({ text: "一、选题的目的、意义（理论、现实）和国内外研究概况", bold: true })]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      // 1.1 选题目的及意义
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "1.1 选题目的及意义", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "随着无人机技术的快速发展和普及，无人机在交通巡检领域的应用越来越广泛。无人机可以高效地执行道路巡查、交通监控、事故现场勘测、违章行为抓拍等任务，大大提高了交通管理的效率和覆盖面。相比于传统的人工巡检方式，无人机巡检具有覆盖面广、效率高、成本低、灵活性强等优势，能够在复杂路况下完成巡查任务，有效降低巡检人员的工作强度和安全风险。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "然而，无人机在执行巡检任务过程中涉及大量的操作行为，包括起飞前的设备检查、航线规划与设置、起飞与降落操控、巡航过程中的传感器控制、数据采集与传输、应急处置等。这些操作的安全性和合规性直接关系到公共安全和任务成效。一旦出现操作失误或违规操作，可能导致无人机失控坠落、侵入禁飞区、干扰民航飞行等严重后果，造成人员伤亡和财产损失。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "近年来，无人机安全事故频发，包括非法飞行、侵入禁飞区、操控失误导致的人员伤亡等。2019年武汉天河机场发生多起无人机入侵事件，严重影响航班正常起降；2021年某地因无人机操作不当导致高压线断裂，造成大面积停电。这些事件凸显了无人机操作安全审计的重要性。传统的无人机管理系统往往缺乏完善的操作记录和审计机制，事后难以追溯和分析安全事故的根本原因，也无法为责任认定和法律取证提供有力支撑。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "从理论层面而言，无人机操作安全审计是信息系统安全审计理论在无人机领域的延伸和应用。信息系统安全审计通过记录和分析系统中的各种操作行为，达到保障系统安全、合规运营的目的。将这一理论应用于无人机系统，可以建立起一套完整的操作行为记录、分析和追溯机制，为无人机的安全管理提供理论支撑。同时，基于哈希链的防篡改技术是当前数据完整性保护的重要手段，将其应用于审计日志存储，可以有效防止日志被恶意篡改，保证审计证据的可靠性。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "从现实层面而言，构建一套完整的无人机交通巡检系统操作安全审计平台，对于保障无人机安全运行、提升交通巡检管理水平、满足监管合规要求具有重要的实际意义。一方面，通过对操作行为的全面记录和分析，可以及时发现操作中的安全隐患和违规行为，预防事故的发生；另一方面，完整的审计记录也为事后分析和责任认定提供了依据，有助于完善无人机安全管理体系。此外，该平台还可以为无人机运营企业的内部管理和监管部门的外部监督提供技术手段，推动无人机产业的健康有序发展。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "本课题旨在设计并实现一个基于AOP（面向切面编程）和哈希链技术的无人机交通巡检系统操作安全审计平台，通过自动记录所有操作行为、保证审计日志的防篡改、实现可疑操作的检测与告警，为无人机交通巡检的安全管理提供技术支撑。",
          size: 21 
        })]
      }),

      // 1.2 国内外研究概况
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "1.2 国内外研究概况", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "在无人机安全审计领域，国内外学者和机构已经开展了大量相关研究。在国外监管层面，美国联邦航空管理局（FAA）制定了完善的无人机运营规范，要求运营方记录并保存飞行日志，同时通过Remote ID系统实现无人机实时识别和追踪。欧盟航空安全局（EASA）发布了《无人机运营条例》，对无人机的注册、运营、飞行员资质等方面做出了明确规定。日本、韩国等国家也建立了无人机实名制登记和飞行监控系统，要求无人机用户注册并遵守相应的飞行规则。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "在技术研究层面，国外的无人机安全研究主要集中于以下几个方向：一是基于区块链的无人机数据管理，利用区块链不可篡改的特性保证飞行数据的真实性和完整性；二是无人机入侵检测，通过分析无人机的飞行轨迹和行为模式，识别潜在的入侵风险；三是无人机隐私保护，研究如何在使用无人机进行监控时保护公民隐私权。这些研究为无人机安全审计技术的发展提供了重要参考。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "国内方面，中国民用航空局发布了《无人驾驶航空器实名制登记管理规定》，要求重量在250克以上的无人机进行实名登记，并明确了无人机飞行管理的相关要求。在学术研究领域，国内学者主要围绕以下几个方面展开研究：一是无人机轨迹追踪与可视化，研究如何利用GPS、北斗等定位技术实现无人机轨迹的实时追踪和历史回放；二是无人机飞行数据存储，研究大规模飞行数据的高效存储和检索方法；三是无人机异常行为检测，通过机器学习等技术识别无人机的异常飞行模式。南京航空航天大学提出了基于物联网的无人机监管系统框架，实现了无人机的注册管理、飞行监控和数据分析等功能；北京航空航天大学开展了无人机操作行为异常检测的相关研究，提出了一种基于深度学习的异常行为识别方法。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "然而，现有研究仍存在以下不足：一是缺乏完整的操作行为记录机制，大多数研究关注的是无人机的飞行轨迹和状态数据，而对地面操控人员的操作行为记录不够全面；二是现有的日志存储方案无法有效防止数据篡改，传统的数据库存储方式可以被有权限的用户修改，难以保证审计证据的可靠性；三是缺少智能化的可疑操作检测和告警功能，现有系统大多只提供简单的日志查询功能，缺乏对异常操作行为的智能分析和预警能力。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "本课题将在现有研究基础上，针对上述问题进行深入研究和系统实现。通过引入AOP技术实现操作行为的自动记录，采用哈希链技术保证日志的防篡改，结合规则引擎和机器学习方法实现可疑操作的智能检测，构建一套完整的无人机交通巡检系统操作安全审计平台。",
          size: 21 
        })]
      }),

      // 二、理论依据、研究内容和研究方法、步骤及进度安排
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun({ text: "二、本课题的理论依据、研究内容和研究方法、步骤及进度安排", bold: true })]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      // 2.1 理论依据
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "2.1 理论依据", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "本研究的理论基础主要包括AOP面向切面编程理论、哈希链防篡改技术和JWT身份认证机制。这些理论和技术在信息安全领域有着广泛的应用，为本课题的研究提供了坚实的技术支撑。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "AOP（Aspect-Oriented Programming，面向切面编程）是一种成熟的程序设计范式，最早由Gregor Kiczales等人于1997年提出。该技术的核心思想是将应用程序中的横切关注点（如日志记录、权限验证、事务管理等）从业务逻辑中分离出来，形成独立的切面（Aspect），从而实现关注点分离。在审计系统设计中，AOP可以将日志记录逻辑与业务代码解耦，通过切面拦截器对目标方法进行增强，自动捕获方法的调用信息，包括方法名、参数、返回值、执行时间等，无需在每个业务方法中手动添加日志代码。这种方式不仅简化了开发工作，更重要的是保证了审计日志的完整性和一致性，避免了因开发人员遗漏而导致的日志缺失。Spring框架提供了完善的AOP支持，使得基于AOP的审计日志实现成为可能。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "哈希链（Hash Chain）技术是保证数据完整性的一种重要手段，其基本原理是将数据块按照时间顺序链接起来，每个数据块包含前一个数据块的哈希值。哈希函数具有单向性和抗碰撞性，即从哈希值无法反推出原始数据，且很难找到两个不同的数据具有相同的哈希值。在审计系统中，每条日志记录都包含上一条日志的哈希值，形成一条完整的链式结构。一旦其中任何一条日志被修改，其哈希值就会发生变化，进而导致后续所有日志的哈希验证失败，从而可以发现数据被篡改的事实。这种技术最初由Leslie Lamport在1981年提出，用于密码认证领域，后来被广泛应用于区块链、时间戳服务等需要保证数据不可篡改的场景。本研究将采用SHA-256哈希算法，它是目前应用最广泛的哈希算法之一，具有安全性高、计算速度快等优点。",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "JWT（JSON Web Token）是一种开放标准（RFC 7519），它定义了一种紧凑且自包含的方式，用于在各方之间安全地传输JSON格式的信息。JWT由三部分组成：头部（Header）、载荷（Payload）和签名（Signature），各部分之间用点号分隔。JWT可以验证消息的发送者身份，确保信息在传输过程中未被篡改。在审计系统中，JWT用于用户身份认证，确保每条操作日志都能追溯到具体的操作人员。同时，JWT也可以携带用户的角色和权限信息，用于实现基于角色的访问控制。",
          size: 21 
        })]
      }),

      // 2.2 研究内容
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "2.2 研究内容", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "本课题主要研究以下内容：",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（1）审计日志数据模型设计：设计合理的审计日志数据结构，包括操作时间、操作用户、操作类型、操作对象、操作描述、IP地址、用户代理、请求方法、请求URL、请求参数、响应状态、哈希值、前驱哈希等字段，为后续的分析和追溯提供完整的数据基础。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（2）基于AOP的操作日志自动记录：利用Spring AOP技术，定义审计日志切面，对控制器的所有方法进行拦截，自动记录用户的操作行为，包括登录登出、数据查询、数据新增、数据修改、数据删除等操作，无需在业务代码中手动添加日志记录逻辑。同时，通过切面表达式精确控制需要拦截的方法，避免对无关方法的干扰。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（3）基于SHA-256哈希链的日志防篡改机制：对每条审计日志计算SHA-256哈希值，并将其与前一条日志的哈希值关联，形成完整的哈希链。当需要验证日志完整性时，通过重新计算哈希值并与存储的哈希值进行比对，可以检测出任何非法篡改行为。同时，提供完整性验证接口，允许管理员随时查看日志的完整性状态。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（4）可疑操作行为检测与告警：建立可疑操作的检测规则，包括短时间内频繁操作（如每秒超过10次）、异常时间段的登录尝试（如凌晨2点至5点）、非授权访问敏感数据、连续登录失败等。当检测到可疑行为时，系统自动生成告警并记录到告警表中，同时通过界面提醒管理员。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（5）无人机状态监控与告警：实现无人机状态数据接收接口，接收无人机实时上报的位置、高度、速度、电量等信息，当检测到无人机进入禁飞区、飞行高度超限、速度异常、电量过低等情况时，自动生成安全告警。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（6）前端数据可视化展示：采用Vue3 + Element Plus + ECharts技术，实现审计日志的查询、筛选、统计图表展示，包括操作类型分布饼图、操作趋势折线图、安全告警统计等，为管理员提供直观的决策支持。同时，实现无人机位置在地图上的实时展示。", size: 21 })]
      }),

      // 2.3 研究方法、步骤及进度安排
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "2.3 研究方法、步骤及进度安排", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ 
          text: "本研究将采用文献研究法、系统设计法和实验验证法相结合的方式进行。文献研究法用于了解国内外研究现状和发展趋势；系统设计法用于完成系统的整体架构和详细设计；实验验证法用于测试系统的功能和性能。具体步骤和进度安排如下：",
          size: 21 
        })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（1）第1周（2025.12.02-2025.12.08）：查阅国内外相关文献，了解无人机安全审计、AOP编程、哈希链技术等领域的最新研究进展，确定研究方向和重点，完成开题报告的撰写。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（2）第2周（2025.12.09-2025.12.15）：分析系统需求，确定审计平台的功能模块和技术架构，完成系统设计方案的编写，包括系统总体设计、数据库设计、接口设计等。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（3）第3周（2025.12.16-2025.12.22）：搭建开发环境，包括安装JDK、IntelliJ IDEA、Maven、MySQL、Node.js等开发工具，创建Spring Boot项目骨架，配置项目依赖。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（4）第4周（2025.12.23-2025.12.29）：完成数据库表设计，创建用户表、审计日志表、告警表、无人机状态表等，建立数据库连接，实现基本的增删改查功能。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（5）第5周（2025.12.30-2026.01.05）：实现用户认证功能，包括用户注册、登录、权限验证，采用JWT Token进行身份认证，完成安全认证模块的开发。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（6）第6周（2026.01.06-2026.01.12）：实现基于AOP的审计日志记录功能，定义审计日志切面和注解，完成对控制器方法的自动拦截和日志记录。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（7）第7周（2026.01.13-2026.01.19）：实现基于SHA-256哈希链的日志防篡改机制，包括哈希计算、哈希链构建、完整性验证等功能。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（8）第8周（2026.01.20-2026.01.26）：实现可疑操作检测与告警功能，包括检测规则定义、告警生成、告警处理等，完成告警管理模块的开发。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（9）第9周（2026.01.27-2026.02.02）：实现无人机状态监控功能，包括状态数据接收接口、状态存储、实时位置展示等，完成无人机监控模块的开发。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（10）第10周（2026.02.03-2026.02.09）：搭建Vue3前端开发环境，创建项目骨架，配置路由和状态管理，实现登录页面和主页面布局。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（11）第11周（2026.02.10-2026.02.16）：实现前端审计日志查询界面，包括日志列表展示、条件筛选、分页查询等功能。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（12）第12周（2026.02.17-2026.02.23）：实现前端数据可视化功能，包括操作类型分布图、操作趋势图、告警统计图等，使用ECharts实现丰富的图表展示。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（13）第13周（2026.02.24-2026.03.02）：实现无人机模拟器功能，提供可视化的界面用于模拟无人机状态上报，方便系统测试，完成系统集成和联调。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（14）第14周（2026.03.03-2026.03.09）：进行系统测试，包括功能测试、性能测试、安全测试等，修复发现的问题，完善系统功能。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（15）第15周（2026.03.10-2026.03.16）：撰写毕业论文，包括摘要、引言、相关技术、系统设计、系统实现、系统测试、结论等章节，确保字数达到要求。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（16）第16周（2026.03.17-2026.03.23）：完善毕业论文，检查格式和内容，准备答辩PPT，进行答辩演练。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（17）第17周（2026.03.24-2026.03.30）：根据答辩反馈意见进行论文修改，提交最终定稿，准备答辩材料。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（18）第18周（2026.03.31-2026.05.30）：完成答辩，根据答辩委员会意见进行最终修改，提交毕业论文及相关材料。", size: 21 })]
      }),

      // 三、本课题的重点、难点
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun({ text: "三、本课题的重点、难点，预期结果和成果形式", bold: true })]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      // 3.1 本课题的重点
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "3.1 本课题的重点", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（1）基于AOP的审计日志自动记录机制：设计并实现审计日志切面拦截器，通过自定义注解和切面表达式，对所有业务操作进行自动拦截和记录，确保审计日志的完整性和及时性。这部分的重点在于准确捕获方法的调用信息，包括方法参数、返回值、执行时间等，同时要注意避免对系统性能造成过大影响。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（2）基于SHA-256哈希链的日志防篡改机制：设计哈希链数据结构，实现日志完整性的自动验证。每条日志记录都需要包含上一条日志的哈希值，通过哈希函数的链式传递，形成一条不可分割的日志链。当需要验证日志完整性时，只需要重新计算所有日志的哈希值并与存储的值进行比对，即可发现任何篡改行为。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（3）可疑操作检测与告警系统：建立科学合理的检测规则，包括频率检测、时段检测、异常行为检测等，实现对操作风险的实时评估和告警。这部分需要平衡误报率和漏报率，既要保证能够及时发现真正的异常行为，又要避免产生过多无意义的告警。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（4）前端数据可视化展示：采用现代化的前端技术，实现直观、美观的数据展示界面，包括地图展示、统计图表、日志列表等，为管理员提供便捷的管理工具。", size: 21 })]
      }),

      // 3.2 本课题的难点
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "3.2 本课题的难点", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（1）审计性能与系统开销的平衡：AOP切面会在一定程度上影响系统性能，每次方法调用都会触发切面执行，增加响应时间。需要优化切面表达式，只拦截必要的控制器方法，同时采用异步日志记录等方式，减少对正常业务的影响。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（2）哈希链验证效率：随着日志数据量的增长，哈希链验证的计算成本会逐渐增加。需要在验证效率和验证范围之间取得平衡，可以采用分段验证、增量验证等方式提高验证效率。同时，哈希链的存储也需要考虑空间效率问题。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（3）可疑行为检测的准确性：建立科学合理的检测规则是难点之一。规则过于宽松会导致漏报，规则过于严格会产生误报。需要结合实际业务场景，不断调整和优化检测规则，可以通过机器学习算法辅助判断，提高告警的准确性。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（4）前后端数据交互的安全性：前端直接与后端API交互，需要处理好身份认证、权限控制、数据传输安全等问题。采用JWT Token进行认证，同时使用HTTPS协议保证数据传输安全。", size: 21 })]
      }),

      // 3.3 预期结果和成果形式
      new Paragraph({
        heading: HeadingLevel.HEADING_3,
        children: [new TextRun({ text: "3.3 预期结果和成果形式", bold: true })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（1）完成一套完整的无人机交通巡检系统操作安全审计平台，包括基于Spring Boot的后端服务和基于Vue3的前端界面，能够正常运行并提供完整的审计功能。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（2）实现基于AOP的自动日志记录功能，能够完整记录所有用户操作行为，包括登录、查询、修改、删除等操作，日志内容详细、准确。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（3）实现基于SHA-256哈希链的日志防篡改机制，能够验证审计日志的完整性，当日志被篡改时能够及时发现并告警。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（4）实现可疑操作检测与告警功能，能够对异常操作行为进行实时检测和通知，包括频繁操作检测、异常时段登录检测等。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（5）实现无人机状态监控功能，能够接收无人机上报的状态数据，并在地图上展示无人机的实时位置，当检测到异常状态时自动生成告警。", size: 21 })]
      }),
      new Paragraph({
        spacing: { line: 360 },
        children: [new TextRun({ text: "（6）完成毕业论文（设计）说明书一份，字数不少于15000字，内容完整、结构清晰、格式规范。", size: 21 })]
      }),

      // 四、参考文献
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun({ text: "四、参考文献", bold: true })]
      }),
      new Paragraph({ children: [new TextRun({ text: " ", size: 20 })] }),

      new Paragraph({ children: [new TextRun({ text: "[1] Federal Aviation Administration. Unmanned Aircraft Systems. https://www.faa.gov/uas", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[2] Kim S, Lee J. Blockchain-based UAV flight data management system. Journal of Aerospace Information Systems, 2020, 17(3): 123-135.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[3] 王伟, 张明. 基于物联网的无人机监管系统设计. 南京航空航天大学学报, 2019, 51(4): 45-52.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[4] 李强, 陈军. 无人机操作行为异常检测研究. 北京航空航天大学学报, 2021, 47(2): 78-85.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[5] Kiczales G, Lamping J, Mendhekar A, et al. Aspect-oriented programming. ECOOP 1997, 1997: 220-242.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[6] Lamport L. Password authentication with insecure communication. Communications of the ACM, 1981, 24(11): 770-772.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[7] 冯登国, 张敏, 张妍. 云环境下数据安全研究. 计算机学报, 2018, 41(12): 245-267.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[8] 张新, 刘鹏. 基于大数据的网络安全态势感知技术. 信息安全研究, 2020, 6(3): 198-205.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[9] 周志华. 机器学习. 北京: 清华大学出版社, 2016.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[10] Stallings W. Network Security Essentials: Applications and Standards. Pearson, 2017.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[11] 柴跃廷, 李晓. 基于区块链的数据完整性保护技术研究. 计算机应用研究, 2020, 37(5): 1285-1289.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[12] 张波, 王丽. JWT认证机制在RESTful API中的应用. 信息安全研究, 2019, 5(8): 712-717.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[13] 陈晓红, 刘洋. 无人机飞行数据采集与处理技术. 航空学报, 2022, 43(4): 125-138.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[14] 赵军, 黄磊. 基于AOP的Web应用日志审计系统设计与实现. 计算机工程与设计, 2020, 41(6): 1585-1592.", size: 18 })] }),
      new Paragraph({ children: [new TextRun({ text: "[15] European Union Aviation Safety Agency. Acceptable Means of Compliance for Unmanned Aircraft Systems. EASA, 2020.", size: 18 })] }),
    ]
  }]
});

// 保存文档
Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync('/Users/lxxxt/毕设/张庆晗_毕业论文开题报告.docx', buffer);
  console.log('文档已创建');
});
